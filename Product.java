
package comjspiders.pojo;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    private String title;
    private double price;
    private String description;
    private String category;
    private String image;
    private boolean sold;
    private Date dateOfSale; 
    
    
    
}
