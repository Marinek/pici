package pici.database.dpos;

import java.nio.file.Path;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity(name = "directory")
@Table(name = "directories")
@Getter
@Setter
@ToString
public class DirectoryDPO {

	  @Id
	  @GeneratedValue(strategy=GenerationType.AUTO)
	  private Long id;
	  
	  @Column(unique = true)
	  private Path path;
	  
	  @OneToMany(mappedBy = "directory")
	  @JsonIgnore
	  private Set<FileDPO> files;
	  
	  private Integer count;
	  
	  public String getPathName() {
		  return path.toString();
	  }
}
