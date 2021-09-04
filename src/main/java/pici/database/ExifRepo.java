package pici.database;

import org.springframework.data.repository.CrudRepository;

import pici.database.dpos.ExifTagDPO;

public interface ExifRepo extends CrudRepository<ExifTagDPO, Long> {

	
}
