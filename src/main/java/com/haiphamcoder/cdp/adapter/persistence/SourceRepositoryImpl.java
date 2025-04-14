package com.haiphamcoder.cdp.adapter.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.haiphamcoder.cdp.domain.entity.Source;
import com.haiphamcoder.cdp.domain.repository.SourceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
interface SourceJpaRepository extends JpaRepository<Source, Long> {

}

@Component
@RequiredArgsConstructor
@Slf4j
public class SourceRepositoryImpl implements SourceRepository {

    private final SourceJpaRepository sourceJpaRepository;

    @Override
    public Optional<Source> getSourceById(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSourceById'");
    }

    @Override
    public List<Source> getAllSourcesByUserIdAndFolderId(Long userId, Long folderId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllSourcesByUserIdAndFolderId'");
    }

    @Override
    public List<Source> getAllSourcesByUserIdAndFolderIdAndKeyword(Long userId, Long folderId, String keyword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllSourcesByUserIdAndFolderIdAndKeyword'");
    }

    @Override
    public List<Source> getAllSourcesByUserIdAndFolderIdAndConnectorType(Long userId, Long folderId,
            Integer connectorType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getAllSourcesByUserIdAndFolderIdAndConnectorType'");
    }

    @Override
    public List<Source> getAllSourcesByUserIdAndFolderIdAndStatus(Long userId, Long folderId, Integer status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllSourcesByUserIdAndFolderIdAndStatus'");
    }

    @Override
    public List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimit(Long userId, Long folderId, Integer page,
            Integer limit) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getAllSourcesByUserIdAndFolderIdAndPageAndLimit'");
    }

    @Override
    public List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndKeyword(Long userId, Long folderId,
            Integer page, Integer limit, String keyword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndKeyword'");
    }

    @Override
    public List<Source> getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndConnectorType(Long userId, Long folderId,
            Integer page, Integer limit, Integer connectorType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'getAllSourcesByUserIdAndFolderIdAndPageAndLimitAndConnectorType'");
    }

}
