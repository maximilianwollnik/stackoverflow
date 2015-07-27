package consumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode
@RequiredArgsConstructor
@AllArgsConstructor
public class Product {
	private long id;
	private String name;
	private double price;
}
