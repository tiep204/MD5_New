package ra.model.service.mapper;

import org.springframework.stereotype.Component;
import ra.model.entity.Catalog;
import ra.payload.request.CatalogRequest;
import ra.payload.response.CatalogResponse;

import java.util.Date;

@Component
public class CatalogMapper implements IGenericMapper<Catalog, CatalogRequest, CatalogResponse>{
    @Override
    public Catalog toEntity(CatalogRequest catalogRequest) {
        return Catalog.builder()
                .catalogName(catalogRequest.getCatalogName())
                .catalogTitle(catalogRequest.getCatalogTitle())
                .catalogStatus(catalogRequest.isCatalogStatus())
                .created(new Date())
                .catalogStatus(true).build();
    }

    @Override
    public CatalogResponse toResponse(Catalog catalog) {
        return CatalogResponse.builder()
                .catalogID(catalog.getCatalogID())
                .catalogName(catalog.getCatalogName())
                .catalogTitle(catalog.getCatalogTitle())
                .created(catalog.getCreated())
                .catalogStatus(catalog.isCatalogStatus())
                .build();
    }
}