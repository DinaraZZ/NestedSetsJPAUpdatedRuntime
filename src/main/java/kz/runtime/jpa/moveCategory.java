package kz.runtime.jpa;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import kz.runtime.jpa.entity.Tree;

public class moveCategory {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        moveCategory("2", "0", manager); // Процессоры в аудиотехнику
//        manager.clear();
//        showTree.showTree(manager);
    }

    public static void moveCategory(String fromId, String toId, EntityManager manager) {
        int indexInt = Integer.parseInt(toId);

        Tree to = manager.find(Tree.class, Integer.parseInt(toId));
        Tree from = manager.find(Tree.class, Integer.parseInt(fromId));
        if (to == null || from.getRightKey() < to.getLeftKey() ||
                from.getLevel() >= to.getLevel()) {
            manager.clear();
            try {
                manager.getTransaction().begin();

                Tree fromCategory = manager.find(Tree.class, Integer.parseInt(fromId));
                long fromLeftKey = fromCategory.getLeftKey();
                long fromRightKey = fromCategory.getRightKey();
                long size = fromRightKey - fromLeftKey + 1;


                // обновить ключи нужной кучки
                Query updateQuery = manager.createQuery(
                        """
                                update Tree t
                                set t.leftKey = -1 * t.leftKey,
                                t.rightKey = -1 * t.rightKey
                                where t.leftKey >= ?1
                                and t.rightKey <= ?2
                                """
                );
                updateQuery.setParameter(1, fromLeftKey);
                updateQuery.setParameter(2, fromRightKey);
                updateQuery.executeUpdate();


                // поменять ключи родителей
                Query parentCategoryQuery = manager.createQuery(
                        """
                                update Tree t
                                set t.rightKey = t.rightKey - ?1
                                where t.leftKey < ?2
                                and t.rightKey > ?3
                                 """
                );
                parentCategoryQuery.setParameter(1, size);
                parentCategoryQuery.setParameter(2, fromLeftKey);
                parentCategoryQuery.setParameter(3, fromRightKey);
                parentCategoryQuery.executeUpdate();

                // поменять ключи всех нижних
                Query nextCategoriesQuery = manager.createQuery(
                        """
                                  update Tree t 
                                  set t.leftKey = t.leftKey - ?1,
                                  t.rightKey = t.rightKey - ?1 
                                  where t.leftKey > ?2
                                """
                );
                nextCategoriesQuery.setParameter(1, size);
                nextCategoriesQuery.setParameter(2, fromRightKey);
                nextCategoriesQuery.executeUpdate();


                if (indexInt > 0) { // перенос в существующие ветки
                    Tree toCategory = manager.find(Tree.class, Integer.parseInt(toId));
                    long toLeftKey = toCategory.getLeftKey();
                    long toRightKey = toCategory.getRightKey();

                    // обновить ключи нижних подкатегорий с учётом переноса
                    Query subCategoriesQuery = manager.createQuery(
                            """
                                    update Tree t
                                    set t.leftKey = t.leftKey + ?1,
                                    t.rightKey = t.rightKey + ?1
                                    where t.leftKey > ?2
                                    """
                    );
                    subCategoriesQuery.setParameter(1, size);
                    subCategoriesQuery.setParameter(2, toRightKey);
                    subCategoriesQuery.executeUpdate();

                    //обновить ключи пачки переносимой
                    long summand = toRightKey - fromLeftKey;
                    long levelSummand = toCategory.getLevel() + 1 - fromCategory.getLevel();
                    Query updateQueryPack = manager.createQuery(
                            """
                                    update Tree t
                                    set t.leftKey = abs(t.leftKey) + ?1,
                                    t.rightKey = abs(t.rightKey) + ?1,
                                    t.level = t.level + ?2
                                    where t.leftKey < 0
                                    """
                    );
                    updateQueryPack.setParameter(1, summand);
                    updateQueryPack.setParameter(2, levelSummand);
                    updateQueryPack.executeUpdate();

                    // обновить правый ключ родительской категории
                    Query parentRightKeyUpdate = manager.createQuery(
                            """
                                    update Tree t
                                    set t.rightKey = t.rightKey + ?1
                                    where t.leftKey <= ?2
                                    and t.rightKey >= ?3
                                    """
                    );
                    parentRightKeyUpdate.setParameter(1, size); // аудит - 21
                    parentRightKeyUpdate.setParameter(2, toLeftKey);
                    parentRightKeyUpdate.setParameter(3, toRightKey);
                    parentRightKeyUpdate.executeUpdate();
                } else { // перенос в качестве новой родительской ветки
                    TypedQuery<Long> query = manager.createQuery(
                            "select max(t.rightKey) from Tree t", Long.class
                    );
                    Long maxRightKey = query.getSingleResult();

                    long summand = maxRightKey + 1 - fromLeftKey;

                    Query updateNode = manager.createQuery("""
                            update Tree t
                            set t.leftKey = abs(t.leftKey) + ?1,
                            t.rightKey = abs(t.rightKey) + ?1,
                            t.level = t.level - ?2
                            where t.leftKey < 0
                            """);
                    updateNode.setParameter(1, summand);
                    updateNode.setParameter(2, fromCategory.getLevel());
                    updateNode.executeUpdate();
                }

                manager.getTransaction().commit();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                manager.getTransaction().rollback();
            }
        } else {
            System.out.println("Невозможно перенсти категорию в свою подкатегорию.");
        }
    }
}
