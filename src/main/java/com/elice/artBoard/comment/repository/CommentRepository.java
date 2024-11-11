package com.elice.artBoard.comment.repository;


import com.elice.artBoard.comment.domain.Comment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepository {

    @PersistenceContext
    EntityManager em;

    public Comment save(Comment comment) {
        em.persist(comment);
        return comment;
    }

    public Optional<Comment> findById(Long commentId) {
        Comment comment = em.find(Comment.class, commentId);
        return Optional.ofNullable(comment);
    }

    public List<Comment> findAll(Long postId) {
        return em.createQuery("select c from Comment c " +
                        "join fetch c.post p " +
                        "where p.id = :postId", Comment.class)
                .setParameter("postId", postId)
                .getResultList();
    }

    public void delete(Comment comment) {
        em.remove(comment);
    }

    public void deleteByPostId(Long postId) {
        em.createQuery("delete from Comment c where c.post.id = :postId")
                .setParameter("postId", postId)
                .executeUpdate();
    }
}
