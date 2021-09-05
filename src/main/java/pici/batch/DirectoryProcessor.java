package pici.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import lombok.extern.slf4j.Slf4j;
import pici.database.FileRepo;
import pici.database.dpos.DirectoryDPO;
import pici.database.dpos.ExifTagDPO;
import pici.database.dpos.FileDPO;

@Component
@Slf4j
public class DirectoryProcessor implements ItemProcessor<DirectoryDPO, DirectoryDPO> {

	@Autowired
	private FileRepo fileRepo;
	
	@Override
	public DirectoryDPO process(final DirectoryDPO directoryDPO) throws Exception {
		
		log.error("Repo Size: " + fileRepo.count());
		
		Files.walkFileTree(directoryDPO.getPath(), new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				log.info("preVisitDirectory: " + dir.toString());
				
				if( Files.isSameFile(dir, directoryDPO.getPath()) ) {
					return FileVisitResult.CONTINUE;
				}
				
				return FileVisitResult.SKIP_SUBTREE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				log.info("Visiting file: " + file);

				boolean isExisting = isFileExisting(file, directoryDPO);
				
				if(isExisting) {
					log.info("Skipping (already known) file: " + file);
					return FileVisitResult.CONTINUE;
				}

				FileDPO newFile = new FileDPO();

				boolean isImage = getExifData(file, newFile);

				if(!isImage) {
					log.info("Skipping (no image) file: " + file);
					return FileVisitResult.CONTINUE;
				}

				newFile.setDirectory(directoryDPO);
				newFile.setFileName(file.getFileName().toString());
				
				try {
					newFile.setContentHash(getMD5Checksum(file));
				} catch (NoSuchAlgorithmException | IOException e) {
					log.error("Error during md5 checksum generation", e);
				} 
				newFile.setMimeType(null);
								
				newFile = fileRepo.save(newFile);

				return FileVisitResult.CONTINUE;
			}

			private boolean isFileExisting(Path file, DirectoryDPO directoryDPO) {
				return fileRepo.findInDirectory(file.getFileName().toString(), directoryDPO.getId()).size() > 0;
			}

			private boolean getExifData(Path file, FileDPO fileDPO) throws IOException {

				try {
					Metadata metadata = ImageMetadataReader.readMetadata(file.toFile());

					for (Directory directory : metadata.getDirectories()) {

						for(Tag tag : directory.getTags()) {

							fileDPO.addExifTag(new ExifTagDPO(tag));
						}

					}
				} catch (Throwable e) { // TODO: Due to an error in the library we have to be very restrictive. 
					if(log.isTraceEnabled()) {
						log.trace("NOTHING - Skipping file because it is not an image", e);
					}
					return false;
				}

				return true;

			}

			private String getMD5Checksum(Path file) throws NoSuchAlgorithmException, IOException {
				InputStream fis =  new FileInputStream(file.toString());

				byte[] buffer = new byte[1024];
				MessageDigest complete = MessageDigest.getInstance("MD5");
				int numRead;

				do {
					numRead = fis.read(buffer);
					if (numRead > 0) {
						complete.update(buffer, 0, numRead);
					}
				} while (numRead != -1);

				fis.close();
				byte[] b =   complete.digest();
				String result = ""; 
				for (int i=0; i < b.length; i++) {
					result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
				}
				return result;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
				log.error("Error while visiting File: " + file, exc);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				log.info("PostVisitDirectory()" + dir.toString());

				boolean finishedSearch = Files.isSameFile(dir, directoryDPO.getPath());
				if (finishedSearch) {
					return FileVisitResult.TERMINATE;
				}
				directoryDPO.setScheduledScan(false);
				return FileVisitResult.CONTINUE;
			}
		});
		
		return directoryDPO;
	}

}
