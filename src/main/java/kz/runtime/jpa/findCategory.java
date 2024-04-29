package kz.runtime.jpa;

import jakarta.persistence.*;
import kz.runtime.jpa.entity.Tree;

import java.util.List;

public class FindCategory {
    public static void findByCategory(String category, EntityManagerFactory factory) {
        EntityManager manager = factory.createEntityManager();
        try {
            TypedQuery<Tree> treeQuery = manager.createQuery(
                    "select t from Tree t where t.name = ?1", Tree.class);
            treeQuery.setParameter(1, category);

            Tree categoryObj = treeQuery.getSingleResult();
            Long categoryLeftKey = categoryObj.getLeftKey();
            Long categoryRightKey = categoryObj.getRightKey();
            int categoryLevel = categoryObj.getLevel();

            if (categoryRightKey - categoryLeftKey == 1) System.out.println(categoryObj.getName());
            else {
                treeQuery = manager.createQuery(
                        "select t from Tree t where t.leftKey between ?1 and ?2", Tree.class
                );
                treeQuery.setParameter(1, categoryLeftKey);
                treeQuery.setParameter(2, categoryRightKey);

                List<Tree> resultTree = treeQuery.getResultList();

                for (Tree tree : resultTree) {
                    System.out.printf("%s%s\n", "- ".repeat(tree.getLevel() - categoryLevel), tree.getName());
                }
            }

        } catch (NoResultException noResultException) {
            System.out.println("Данной категории не существует.");
        }
    }


}
