package bg.project.json.data.entities;

import jakarta.persistence.*;

import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;

@Entity
@Table(name = "categories")
public class Category extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "categories", fetch = FetchType.EAGER)
    private Set<Product> products;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
