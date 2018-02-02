package com.paycr.common.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.paycr.common.data.domain.ContactUs;

@Repository
public interface ContactUsRepository extends JpaRepository<ContactUs, Integer> {

	@Transactional
	@Query("SELECT cu FROM ContactUs cu WHERE resolved = ?1 AND type = ?2 ORDER BY id DESC")
	public List<ContactUs> findByType(boolean resolved, String type, Pageable pageable);

	@Transactional
	@Query("SELECT cu FROM ContactUs cu WHERE resolved = ?1 AND email = ?2 ORDER BY id DESC")
	public List<ContactUs> findByEmail(boolean resolved, String email, Pageable pageable);

	@Transactional
	@Query("SELECT cu FROM ContactUs cu WHERE resolved = ?1 AND email = ?2 AND type = ?3 ORDER BY id DESC")
	public List<ContactUs> findByEmailAndType(boolean resolved, String email, String type, Pageable pageable);

	@Transactional
	@Query("SELECT cu FROM ContactUs cu WHERE resolved = ?1 ORDER BY id DESC")
	public List<ContactUs> findLatest(boolean resolved, Pageable pageable);

}
