package ra.model.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ra.model.entity.Catalog;
import ra.payload.request.CatalogRequest;
import ra.payload.response.CatalogResponse;

import java.util.List;

public interface CatalogService {
    List<CatalogResponse> findAll();

    Catalog findById(int catalogID);

    Catalog saveOrUpdate(Catalog catalog);

    Catalog save(Catalog catalog);

    CatalogResponse update(CatalogRequest catalog,int id);

    List<Catalog> searchByName(String catalogName);

    Page<Catalog> getPaging(Pageable pageable);
}
