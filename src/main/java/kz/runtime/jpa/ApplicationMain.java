package kz.runtime.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static kz.runtime.jpa.AddCategory.*;
import static kz.runtime.jpa.DeleteCategory.*;
import static kz.runtime.jpa.FindCategory.*;
import static kz.runtime.jpa.MoveCategory.*;
import static kz.runtime.jpa.ShowTree.*;

public class ApplicationMain {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");

        try (
                InputStreamReader streamReader = new InputStreamReader(System.in);
                BufferedReader bufferedReader = new BufferedReader(streamReader)) {
            while (true) {

                System.out.println("""
                        \t1. Показать дерево
                        \t2. Найти категорию
                        \t3. Добавить категорию
                        \t4. Удалить категорию
                        \t5. Переместить категорию
                        """);
                System.out.print("Выберите действие: ");
                String index = bufferedReader.readLine();

                int indexNum = Integer.parseInt(index);
                switch (indexNum) {
                    case 1:
                        showTree(factory);
                        break;
                    case 2:
                        System.out.print("\nВведите название категории: ");
                        String categoryFind = bufferedReader.readLine();
                        findByCategory(categoryFind, factory);
                        break;
                    case 3:
                        showTree(factory);
                        System.out.print("\n\nВведите название новой категории: ");
                        String categoryAdd = bufferedReader.readLine();
                        System.out.print("\nВведите индекс новой категории: ");
                        String indexAdd = bufferedReader.readLine();
                        addCategory(indexAdd, categoryAdd, factory);
                        break;
                    case 4:
                        showTree(factory);
                        System.out.print("\n\nВведите индекс удаляемой категории: ");
                        String indexDelete = bufferedReader.readLine();
                        deleteCategory(indexDelete, factory);
                        break;
                    case 5:
                        showTree(factory);
                        System.out.print("\n\nВведите индекс перемещаемой категории: ");
                        String indexMoveFrom = bufferedReader.readLine();
                        System.out.print("\nВведите индекс категории, куда нужно переместить: ");
                        String indexMoveTo = bufferedReader.readLine();
                        moveCategory(indexMoveFrom, indexMoveTo, factory);
                        break;
                    default:
                        break;
                }
                System.out.println();
            }
        } catch (IOException e) {
        }

//        addCategory("11", "Бытовая техника", factory);
//        deleteCategory("2", factory);
    }
}
