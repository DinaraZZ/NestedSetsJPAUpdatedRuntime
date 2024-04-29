package kz.runtime.jpa;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import static kz.runtime.jpa.AddCategory.*;
import static kz.runtime.jpa.DeleteCategory.*;
import static kz.runtime.jpa.FindCategory.*;
import static kz.runtime.jpa.MoveCategory.*;
import static kz.runtime.jpa.ShowTree.*;

public class ApplicationMain {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");

//        addCategory("11", "");
    }
}
