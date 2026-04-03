package com.aahar.Aahar.Repository;

import com.aahar.Aahar.Entity.PortionKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PortionKeywordRepo extends JpaRepository<PortionKeyword , Long > {
    List<PortionKeyword> findByKeywordIn(Collection<String> keywords);
}
