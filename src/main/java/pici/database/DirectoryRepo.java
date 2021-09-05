package pici.database;

import java.nio.file.Path;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import pici.database.dpos.DirectoryDPO;

public interface DirectoryRepo extends PagingAndSortingRepository<DirectoryDPO, Long> {

	@Query("select d from directory d where d.path = ?1")
	public Optional<DirectoryDPO> findOneByPath(Path path);
	
	
	@Query("select d from directory d where d.ignore = false and scheduledScan = true")
	public Page<DirectoryDPO> listDirectoriesForScan(Pageable pageable);
}
