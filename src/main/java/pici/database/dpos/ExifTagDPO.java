package pici.database.dpos;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.drew.metadata.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "exiftag")
@Table(name = "exiftags")
@Getter
@Setter
@ToString
public class ExifTagDPO {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name="file_id")
	@JsonIgnore
	private FileDPO file;
	
	private String tagName;
	
	private String directoryName;

	private String tagDescription;
	
	public ExifTagDPO() {
		
	}
	
	
	public ExifTagDPO(Tag tag) {
		this.directoryName = tag.getDirectoryName();
		tagName = tag.getTagName();
		setTagDescription(tag.getDescription());
	}


	public void setTagDescription(String tagDescription) {
		if(tagDescription != null) {
			this.tagDescription = tagDescription.substring(0, Math.min(254, tagDescription.length()));
		}
	}
}
