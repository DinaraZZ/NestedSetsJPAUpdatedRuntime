package kz.runtime.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import kz.runtime.jpa.entity.Tree;

import java.util.List;

public class ShowTree {
    public static void showTree(EntityManagerFactory factory) {
        EntityManager manager = factory.createEntityManager();
        TypedQuery<Tree> treeQuery = manager.createQuery(
                "select t from Tree t order by t.leftKey", Tree.class);
        List<Tree> treeList = treeQuery.getResultList();

        for (Tree tree : treeList) {
            System.out.printf("%s%s (%d)\n", "- ".repeat(tree.getLevel().intValue()), tree.getName(), tree.getId());
        }
    }
}