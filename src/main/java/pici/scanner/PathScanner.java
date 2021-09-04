package pici.scanner;

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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import lombok.extern.slf4j.Slf4j;
import pici.database.DirectoryRepo;
import pici.database.FileRepo;
import pici.database.dpos.DirectoryDPO;
import pici.database.dpos.ExifTagDPO;
import pici.database.dpos.FileDPO;

@Component
@Slf4j
public class PathScanner {

	@Autowired
	private DirectoryRepo dirRepo;

	@Autowired
	private FileRepo fileRepo;

	@PostConstruct
	public void init() {
		log.trace("PathScanner.init()");
	}

	public Map<Path, DirectoryDPO> scan(Path path) throws IOException {
		final Map<Path, DirectoryDPO> explodePath = new HashMap<Path, DirectoryDPO>();

		this.scan(path, explodePath);

		return explodePath;
	}


	private Map<Path, DirectoryDPO> scan(Path path, final Map<Path, DirectoryDPO> explodePath) throws IOException {

		Files.walkFileTree(path, new FileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				log.info("preVisitDirectory: " + dir.toString());

				DirectoryDPO directoryDPO = dirRepo.findOneByPath(dir).orElse(new DirectoryDPO());

				directoryDPO.setPath(dir);
				directoryDPO.setCount(0);

				directoryDPO = dirRepo.save(directoryDPO);

				explodePath.put(dir,  directoryDPO);

				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				log.info("Visiting file: " + file);

				DirectoryDPO directoryDPO = explodePath.get(file.getParent());
				
				boolean isExisting = isFileExisting(file, directoryDPO);
				
				if(isExisting) {
					return FileVisitResult.CONTINUE;
				}

				FileDPO newFile = new FileDPO();

				boolean isImage = getExifData(file, newFile);

				if(!isImage) {
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
				System.err.println(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				System.out.println("PathScanner.scan(...).new FileVisitor() {...}.postVisitDirectory()" + dir.toString());

				boolean finishedSearch = Files.isSameFile(dir, path);
				if (finishedSearch) {
					System.out.println("-------------------------------");
					return FileVisitResult.TERMINATE;
				}

				return FileVisitResult.CONTINUE;
			}


		});

		return explodePath;

	}

}
