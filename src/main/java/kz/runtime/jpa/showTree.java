package kz.runtime.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import kz.runtime.jpa.entity.Tree;

import java.util.List;

public class showTree {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        showTree(manager);
    }

    public static void showTree(EntityManager manager) {
        TypedQuery<Tree> treeQuery = manager.createQuery(
                "select t from Tree t order by t.leftKey", Tree.class);
        List<Tree> treeList = treeQuery.getResultList();

        for (Tree tree : treeList) {
            System.out.printf("%s%s\n", "- ".repeat(tree.getLevel().intValue()), tree.getName());
        }
    }

}
