package pici.database;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import pici.database.dpos.FileDPO;

public interface FileRepo extends CrudRepository<FileDPO, Long> {

	@Query("SELECT f FROM file f where f.id in (select MAX(fb.id) from file fb where fb.isResolved = false group by fb.contentHash having count(*) >1) ORDER by f.contentHash asc")
	public Page<FileDPO> listDuplicates(Pageable pageable);
	
	@Query("SELECT f from file f where f.fileName=?1 and f.directory.id = ?2")
	public List<FileDPO> findInDirectory(String fileName, Long directory);

	@Query("SELECT f from file f where f.directory not in (SELECT d.id FROM directory d where PATH like ?1)")
	public Page<FileDPO> findOutOfMainDirectory(String mainDirectory, Pageable pageable);
	
	@Modifying
	@Query("update file f set f.isResolved=true where f.contentHash = ?1")
	public int markAsResolved(String md5);

	public List<FileDPO> findByContentHash(String contentHash);
	
	
	
}
