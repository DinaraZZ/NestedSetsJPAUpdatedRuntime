package kz.runtime.jpa.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tree")
public class Tree {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "left_key")
    private Long leftKey;

    @Column(name = "right_key")
    private Long rightKey;

    @Column(name = "level")
    private Integer level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLeftKey() {
        return leftKey;
    }

    public void setLeftKey(Long leftKey) {
        this.leftKey = leftKey;
    }

    public Long getRightKey() {
        return rightKey;
    }

    public void setRightKey(Long rightKey) {
        this.rightKey = rightKey;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
