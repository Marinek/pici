package pici.database.dpos;

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

	@Override
	public String toString() {
		return "FileDPO [id=" + id + ", directory=" + directory.getPathName() + ", fileName=" + fileName
				+ ", contentHash=" + contentHash + ", mimeType=" + mimeType + ", isOriginal=" + isOriginal
				+ ", isResolved=" + isResolved + "]";
	}
	
	
}
