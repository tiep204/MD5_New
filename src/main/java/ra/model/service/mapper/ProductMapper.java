package ra.model.service.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ra.model.entity.Product;
import ra.model.service.CatalogService;
import ra.payload.request.ProductRequest;
import ra.payload.response.ProductDTO;
import ra.payload.response.ProductShort;

@Component
public class ProductMapper implements IGenericMapper<Product, ProductRequest, ProductDTO> {
    @Autowired
    private CatalogService catalogService;

    @Override
    public Product toEntity(ProductRequest productRequest) {
        return Product.builder()
                .productID(productRequest.getProductID())
                .productName(productRequest.getProductName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .productTitle(productRequest.getProductTitle())
                .descriptions(productRequest.getDescriptions())
                .image(productRequest.getImage())
                .catalog(catalogService.findById(productRequest.getCatalogID()))
                .build();
    }

    @Override
    public ProductDTO toResponse(Product product) {
        return ProductDTO.builder()
                .productName(product.getProductName())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .productTitle(product.getProductTitle())
                .descriptions(product.getDescriptions())
                .image(product.getImage())
                .catalog(product.getCatalog().getCatalogName())
                .listImageLink(product.getListImageLink())
                .productStatus(product.isProductStatus())
                .build();
    }
}
