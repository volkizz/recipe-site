package com.nago.recipesite.dao;

import com.nago.recipesite.model.Instruction;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface InstructionRepository extends PagingAndSortingRepository<Instruction, Long> {
}

