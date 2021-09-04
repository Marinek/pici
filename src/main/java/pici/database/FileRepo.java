package pici.database;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import pici.database.dpos.FileDPO;

public interface FileRepo extends CrudRepository<FileDPO, Long> {

	@Query("SELECT f FROM file f where f.contentHash in (select fb.contentHash from file fb where fb.isResolved = false group by fb.contentHash having count(*) >1) ORDER by f.contentHash asc")
	public Page<FileDPO> listDuplicates(Pageable pageable);
	
	@Query("SELECT f from file f where f.fileName=?1 and f.directory.id = ?2")
	public List<FileDPO> findInDirectory(String fileName, Long directory);
}
