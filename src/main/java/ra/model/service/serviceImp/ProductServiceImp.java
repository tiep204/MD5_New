package ra.model.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.model.entity.Product;
import ra.model.entity.ProductImage;
import ra.model.repository.ProductRepository;
import ra.model.service.CatalogService;
import ra.model.service.ImageService;
import ra.model.service.ProductService;
import ra.model.service.mapper.ProductMapper;
import ra.payload.request.ProductRequest;
import ra.payload.response.ProductDTO;
import ra.payload.response.ProductShort;

import javax.persistence.EntityExistsException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImp implements ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CatalogService catalogService;
    @Autowired
    ImageService imageService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    StorageService storageService;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(int productID) {
        return productRepository.findById(productID).get();
    }

    @Override
    public ProductDTO findByIdD(int productID) {
        Optional<Product> productDTO = productRepository.findById(productID);
        if (productDTO.isPresent()){
            return productMapper.toResponse(productDTO.get());
        }
        return null;
    }

    @Override
    public Product saveOrUpdate(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> searchByName(String productName) {
        return productRepository.searchByProductNameContaining(productName);
    }

    @Override
    public Page<Product> getPaging(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

/*    @Override
    public Page<ProductShort> getPage(Pageable pageable) {
        return  productRepository.findAll(pageable).stream()
                .map(product -> productMapper.toResponsee(product))
                .collect(Collectors.toList());
    }*/

    @Override
    public Map<String, Object> paging(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = getPaging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("products", products.getContent());
        data.put("total", products.getSize());
        data.put("totalItems", products.getTotalElements());
        data.put("totalPages", products.getTotalPages());
        return data;
    }

    @Override
    public void delete(int productID) {
        productRepository.deleteById(productID);
    }

    @Override
    public void deleteFromWishList(int productID) {

    }

    @Override
    public List<Product> getWishlist(int userId) {
        return productRepository.findProductsInWishlist(userId);
    }

    public List<ProductShort> getAllProductShorts() {
        List<Product> listPro = productRepository.findAll();
        List<ProductShort> listProductShort = new ArrayList<>();
        for (Product product : listPro) {
            if (product.isProductStatus()==true){
                ProductShort productShort = new ProductShort();
                productShort.setProductID(product.getProductID());
                productShort.setProductName(product.getProductName());
                productShort.setProductTitle(product.getProductTitle());
                productShort.setImage(product.getImage());
                productShort.setPrice(product.getPrice());
                productShort.setCatalog(product.getCatalog().getCatalogName());
                listProductShort.add(productShort);
            }
        }
        return listProductShort;
    }

    @Override
    public List<ProductShort> getAllProductShortsAdmin() {
        List<Product> listPro = productRepository.findAll();
        List<ProductShort> listProductShort = new ArrayList<>();
        for (Product product : listPro) {
                ProductShort productShort = new ProductShort();
                productShort.setProductID(product.getProductID());
                productShort.setProductName(product.getProductName());
                productShort.setProductTitle(product.getProductTitle());
                productShort.setImage(product.getImage());
                productShort.setPrice(product.getPrice());
                productShort.setCatalog(product.getCatalog().getCatalogName());
                listProductShort.add(productShort);
        }
        return listProductShort;
    }

    /*@Override
    public Product createProduct(ProductRequest productRequest) {
        try {
            Catalog catalog = catalogService.findById(productRequest.getCatalogID());
            Product newProduct = new Product();
            newProduct.setCatalog(catalog);
            newProduct.setProductName(productRequest.getProductName());
            newProduct.setProductTitle(productRequest.getProductTitle());
            newProduct.setPrice(productRequest.getPrice());
            newProduct.setQuantity(productRequest.getQuantity());
            newProduct.setImage(productRequest.getImage());
            newProduct.setDescriptions(productRequest.getDescriptions());
            newProduct.setProductStatus(true);
            saveOrUpdate(newProduct);

            for (String str : productRequest.getListImageLink()) {
                ProductImage image = new ProductImage();
                image.setUrlImage(str);
                image.setProduct(newProduct);
                imageService.save(image);
            }
            return newProduct;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Có lỗi trong quá trình xử lý vui lòng thử lại!");
        }
    }*/

    @Override
    public ProductDTO createProduct(ProductRequest productRequest) {
        try {
            Product product = productMapper.toEntity(productRequest);
            product.setProductStatus(true);
            List<String> stringList = new ArrayList<>();

            for (MultipartFile str : productRequest.getListImageLink()) {
                stringList.add(storageService.uploadFile(str));
            }

            product.setImage(stringList.get(0));

            List<ProductImage> images = stringList.stream()
                    .map(s -> ProductImage.builder()
                            .urlImage(s)
                            .product(product)
                            .build()).collect(Collectors.toList());
            product.setListImageLink(images);
            return productMapper.toResponse(productRepository.save(product));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Có lỗi trong quá trình xử lý vui lòng thử lại!");
        }
    }

    @Override
    public ProductDTO productDetail(int id) {
        try {
            Product product = findById(id);
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductName(product.getProductName());
            productDTO.setProductTitle(product.getProductTitle());
            productDTO.setDescriptions(product.getDescriptions());
            productDTO.setPrice(product.getPrice());
            productDTO.setQuantity(product.getQuantity());
            productDTO.setImage(product.getImage());
            productDTO.setProductStatus(product.isProductStatus());
            productDTO.setCatalog(product.getCatalog().getCatalogName());
            productDTO.getListImageLink().addAll(product.getListImageLink());
            return productDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể tìm thấy sản phẩm");
        }
    }

//    @Override
//    public void updateProduct(int productID, ProductRequest product) {
//        Catalog catalog = catalogService.findById(product.getCatalogID());
//        Product productUpdate = findById(productID);
//        productUpdate.setProductName(product.getProductName());
//        productUpdate.setProductTitle(product.getProductTitle());
//        productUpdate.setPrice(product.getPrice());
//        productUpdate.setQuantity(product.getQuantity());
//        productUpdate.setImage(product.getImage());
//        productUpdate.setDescriptions(product.getDescriptions());
//        productUpdate.setProductStatus(product.isProductStatus());
//        productUpdate.setCatalog(catalog);
//        for (ProductImage image : productUpdate.getListImageLink()) {
//            imageService.delete(image.getIdImage());
//        }
//        for (String str : product.getListImageLink()) {
//            ProductImage image = new ProductImage();
//            image.setUrlImage(str);
//            image.setProduct(productUpdate);
//            imageService.save(image);
//        }
//    }


    @Override
    public ProductDTO updateProduct(int productID, ProductRequest product) {
        Optional<Product> existingProductOptional = productRepository.findById(productID);

        if (!existingProductOptional.isPresent()) {
            throw new EntityExistsException("ID Product not found");
        }
        Product existingProduct = existingProductOptional.get();
        Product updatedProduct = productMapper.toEntity(product);
        // Copy thông tin từ updatedProduct vào existingProduct
        existingProduct.setProductName(updatedProduct.getProductName());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setQuantity(updatedProduct.getQuantity());
        existingProduct.setProductStatus(updatedProduct.isProductStatus());
        existingProduct.setDescriptions(updatedProduct.getDescriptions());
        existingProduct.setProductTitle(updatedProduct.getProductTitle());
        if (!product.getListImageLink().isEmpty()){
            //
            List<String> listUrl = new ArrayList<>();
            for (MultipartFile multipartFile : product.getListImageLink()){
                String imageUrl = storageService.uploadFile(multipartFile);
                listUrl.add(imageUrl);
            }
            existingProduct.setImage(listUrl.get(0));
            List<ProductImage> images = listUrl.stream()
                    .map(s -> ProductImage.builder()
                            .urlImage(s)
                            .product(existingProduct)
                            .build())
                    .collect(Collectors.toList());
            existingProduct.setListImageLink(images);
        }
        return productMapper.toResponse(productRepository.save(existingProduct));
    }
}