package kz.runtime.jpa;

import jakarta.persistence.*;
import kz.runtime.jpa.entity.Tree;

public class AddCategory {
    public static void addCategory(String index, String newCategoryName, EntityManagerFactory factory) {
        EntityManager manager = factory.createEntityManager();

        try {
            manager.getTransaction().begin();

            int indexInt = Integer.parseInt(index);

            if (indexInt > 0) { // добавление в существующие ветки
                Tree category = manager.find(Tree.class, indexInt);

                // ключи новой категории
                long newLeftKey = category.getRightKey(); //
                long newRightKey = newLeftKey + 1; //

                // изменение верхних категорий
                Query queryPrev = manager.createQuery(
                        """
                                update Tree t
                                set t.rightKey = t.rightKey + 2
                                where t.leftKey < ?1 and t.rightKey > ?2
                                """
                );
                queryPrev.setParameter(1, category.getLeftKey());
                queryPrev.setParameter(2, category.getRightKey());
                queryPrev.executeUpdate();


                // изменение нижних категорий
                Query queryNext = manager.createQuery(
                        """
                                update Tree t
                                set t.leftKey = t.leftKey + 2,
                                t.rightKey = t.rightKey + 2
                                where t.leftKey > ?1
                                """
                );
                queryNext.setParameter(1, category.getRightKey());
                queryNext.executeUpdate();

                // изменение правого ключа главной категории
                category.setRightKey(category.getRightKey() + 2);

                Tree newCategory = new Tree();
                newCategory.setName(newCategoryName);
                newCategory.setLeftKey(newLeftKey);
                newCategory.setRightKey(newRightKey);
                newCategory.setLevel(category.getLevel() + 1);

                manager.persist(newCategory);
            } else { // добавление новой родительской ветки
                TypedQuery<Long> query = manager.createQuery(
                        "select max(t.rightKey) from Tree t", Long.class
                );
                Long maxRightKey = query.getSingleResult();

                Tree newCategory = new Tree();
                newCategory.setName(newCategoryName);
                newCategory.setLeftKey(++maxRightKey);
                newCategory.setRightKey(++maxRightKey);
                newCategory.setLevel(0);

                manager.persist(newCategory);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
        }
    }
    // удаление категории и всего, что внутри
}
