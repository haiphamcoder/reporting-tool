package com.haiphamcoder.cdp.adapter.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.haiphamcoder.cdp.domain.entity.Folder;
import com.haiphamcoder.cdp.domain.repository.FolderRepository;
import com.haiphamcoder.cdp.shared.SnowflakeIdGenerator;

import lombok.RequiredArgsConstructor;

@Repository
interface FolderJpaRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByIdAndUserId(Long id, Long userId);

    List<Folder> findAllByUserId(Long userId);

    List<Folder> findAllByParentFolderId(Long parentFolderId);

}

@Component
@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepository {

    private final FolderJpaRepository folderJpaRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator = SnowflakeIdGenerator.getInstance();

    @Transactional
    @Override
    public Optional<Folder> getFolderById(Long id) {
        return folderJpaRepository.findById(id);
    }

    @Transactional
    @Override
    public Optional<Folder> getFolderByIdAndUserId(Long id, Long userId) {
        return folderJpaRepository.findByIdAndUserId(id, userId);
    }

    @Transactional
    @Override
    public List<Folder> getFoldersByUserId(Long userId) {
        return folderJpaRepository.findAllByUserId(userId);
    }

    @Transactional
    @Override
    public List<Folder> getFoldersByParentFolderId(Long parentFolderId) {
        return folderJpaRepository.findAllByParentFolderId(parentFolderId);
    }

    @Transactional
    @Override
    public Folder saveFolder(Folder folder) {
        Long folderId = folder.getId();
        if (folderId == null) {
            folder.setId(snowflakeIdGenerator.generateId());
        }
        return folderJpaRepository.save(folder);
    }

}
