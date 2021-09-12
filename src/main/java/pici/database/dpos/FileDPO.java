package pici.database.dpos;

import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "file")
@Table(name = "files")
@Getter
@Setter
public class FileDPO {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name="directory_id", nullable=false)
	private DirectoryDPO directory;

	@OneToMany(mappedBy = "file", cascade = {CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE})
	private Set<ExifTagDPO> exifTags = new HashSet<ExifTagDPO>();
	
	private String fileName;

	private String contentHash;

	private String mimeType;
	
    @Column(columnDefinition = "boolean default false")
	private boolean isOriginal = false;
    
    @Column(columnDefinition = "boolean default false")
  	private boolean isResolved = false;

	public void addExifTag(ExifTagDPO exifTagDPO) {
		this.exifTags.add(exifTagDPO);
		exifTagDPO.setFile(this);
		
	}
	
	public Path getDesignationPath() {
		Calendar originalDate = Calendar.getInstance();
		originalDate.setTime(getOriginalDate());
		
		String year = String.valueOf(originalDate.get(Calendar.YEAR)); 
		String month = String.format("%02d", (originalDate.get(Calendar.MONTH) + 1)); 
		return Path.of(year, month);
	}
	
	public String getFileType() {
		for(ExifTagDPO tag : this.exifTags) {
			if(tag.getDirectoryName().contentEquals("File Type")) {
				return tag.getTagDescription();
			}
		}
		
		return "";
	}
	
	public Date getOriginalDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
		//1998:02:09 06:49:00
		for(ExifTagDPO tag : this.exifTags) {
			if(tag.getTagName().contentEquals("Date/Time Original")) {
				Date parse = null;
				try {
					parse = format.parse(tag.getTagDescription());
				} catch (ParseException e) {
					return null;
				}
				return parse;
			}
		}
		
		return null;
	}

	@Override
	public String toString() {
		return "FileDPO [id=" + id + ", directory=" + directory.getPathName() + ", fileName=" + fileName
				+ ", contentHash=" + contentHash + ", mimeType=" + mimeType + ", isOriginal=" + isOriginal
				+ ", isResolved=" + isResolved + "]";
	}
	
	
}
