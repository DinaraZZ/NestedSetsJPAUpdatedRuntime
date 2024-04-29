package kz.runtime.jpa;

import jakarta.persistence.*;
import kz.runtime.jpa.entity.Tree;


public class deleteCategory {

    public static void main(String[] args) {

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        deleteCategory("2", manager);

    }

    public static void deleteCategory(String index, EntityManager manager) {
        try {
            manager.getTransaction().begin();
            Tree categoryDeleted = manager.find(Tree.class, Integer.parseInt(index));
            long leftKey = categoryDeleted.getLeftKey();
            long rightKey = categoryDeleted.getRightKey();
            long size = rightKey - leftKey + 1;

            Query deleteQuery = manager.createQuery(
                    """
                            delete from Tree t
                            where t.leftKey between ?1 and ?2
                            """
            );
            deleteQuery.setParameter(1, leftKey);
            deleteQuery.setParameter(2, rightKey);
            deleteQuery.executeUpdate();

            Query updateQuery = manager.createQuery(
                    """
                            update Tree t 
                            set t.rightKey = t.rightKey - ?1 
                            where t.leftKey < ?2
                            and t.rightKey > ?3
                            """
            );
            updateQuery.setParameter(1, size);
            updateQuery.setParameter(2, leftKey);
            updateQuery.setParameter(3, rightKey);
            updateQuery.executeUpdate();

            Query updateQuery1 = manager.createQuery(
                    """
                              update Tree t 
                              set t.leftKey = t.leftKey - ?1,
                              t.rightKey = t.rightKey - ?1 
                              where t.leftKey > ?2
                            """
            );
            updateQuery1.setParameter(1, size);
            updateQuery1.setParameter(2, rightKey);
            updateQuery1.executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            System.out.println(e);
            manager.getTransaction().rollback();
        }


    }
}
