package ra.model.service.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.model.entity.Catalog;
import ra.model.repository.CatalogRepository;
import ra.model.service.CatalogService;
import ra.model.service.mapper.CatalogMapper;
import ra.payload.request.CatalogRequest;
import ra.payload.response.CatalogResponse;

import javax.persistence.EntityExistsException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = SQLException.class)
public class CatalogServiceImp implements CatalogService {
    @Autowired
    CatalogRepository catalogRepository;
    @Autowired
    private CatalogMapper catalogMapper;
    @Override
    public List<CatalogResponse> findAll() {
        return catalogRepository.findAll().stream()
                .map(c->catalogMapper.toResponse(c))
                .collect(Collectors.toList());
    }

    @Override
    public Catalog findById(int catalogID) {
        return catalogRepository.findById(catalogID).get();
    }

    @Override
    public Catalog saveOrUpdate(Catalog catalog) {
        return catalogRepository.save(catalog);
    }

    @Override
    public Catalog save(Catalog catalog) throws EntityExistsException {
        if (existsByCatalogName(catalog.getCatalogName())){
            throw new EntityExistsException("Error: CatalogName Đã tồn tại!");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date dateNow = new Date();
        String strNow = sdf.format(dateNow);
        try {
            catalog.setCreated(sdf.parse(strNow));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        catalog.setCatalogStatus(true);
        return catalogRepository.save(catalog);
    }

    @Override
    public boolean existsByCatalogName(String name) {
        return catalogRepository.existsByCatalogName(name);
    }

    @Override
    public CatalogResponse update(CatalogRequest catalog,int id) {
        Catalog catalog1 =catalogMapper.toEntity(catalog);
        catalog1.setCatalogID(id);
        catalog1.setCatalogStatus(catalog.isCatalogStatus());
        return catalogMapper.toResponse(catalogRepository.save(catalog1));
    }

    @Override
    public List<Catalog> searchByName(String catalogName) {
        return catalogRepository.searchCatalogByCatalogNameContains(catalogName);
    }

    @Override
    public Page<Catalog> getPaging(Pageable pageable) {
        return catalogRepository.findAll(pageable);
    }

    @Override
    public Map<String,Object> paging(int page, int size) {
        Sort.Order order;
        order = new Sort.Order(Sort.Direction.DESC, "created");
        Pageable pageable = PageRequest.of(page, size, Sort.by(order));
        Page<Catalog> catalogs = getPaging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("catalog", catalogs.getContent());
        data.put("total", catalogs.getSize());
        data.put("totalItems", catalogs.getTotalElements());
        data.put("totalPages", catalogs.getTotalPages());
        return data;
    }
}