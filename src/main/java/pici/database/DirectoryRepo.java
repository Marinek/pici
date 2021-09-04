package pici.database;

import java.nio.file.Path;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import pici.database.dpos.DirectoryDPO;

public interface DirectoryRepo extends CrudRepository<DirectoryDPO, Long> {

	@Query("select d from directory d where d.path = ?1")
	public Optional<DirectoryDPO> findOneByPath(Path path);
	
}
