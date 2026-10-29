package kiruthis_fashions.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku_prefix", nullable = false, unique = true, length = 40)
    private String skuPrefix;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, unique = true, length = 220)
    private String slug;

    @Column(nullable = false, length = 20)
    private String segment; // 'MENS', 'KIDS', 'WOMENS'

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName; // 'EETHMAN', 'HOLSTEINER', 'LOUIS VENTO'

    @Column(name = "brand_edition", length = 120)
    private String brandEdition;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "full_description", columnDefinition = "TEXT")
    private String fullDescription;

    @Column(name = "fabric_composition", nullable = false, length = 120)
    private String fabricComposition;

    @Column(name = "fabric_gsm")
    private Integer fabricGsm;

    @Column(name = "fit_type", nullable = false, length = 40)
    private String fitType;

    @Column(name = "sleeve_type", nullable = false, length = 40)
    private String sleeveType;

    @Column(name = "collar_or_neck", length = 80)
    private String collarOrNeck;

    @Column(name = "pattern_or_print", length = 80)
    private String patternOrPrint;

    @Column(name = "wash_care", length = 200)
    private String washCare;

    @Column(name = "weight_grams", nullable = false)
    private Integer weightGrams = 280;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "is_new_arrival", nullable = false)
    private Boolean isNewArrival = true;

    @Column(name = "is_cod_eligible", nullable = false)
    private Boolean isCodEligible = true;

    @Column(name = "is_returnable", nullable = false)
    private Boolean isReturnable = true;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating = new BigDecimal("4.70");

    @Column(name = "review_count", nullable = false)
    private Integer reviewCount = 18;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<ProductImage> images = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSkuPrefix() { return skuPrefix; }
    public void setSkuPrefix(String skuPrefix) { this.skuPrefix = skuPrefix; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getSegment() { return segment; }
    public void setSegment(String segment) { this.segment = segment; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getBrandEdition() { return brandEdition; }
    public void setBrandEdition(String brandEdition) { this.brandEdition = brandEdition; }

    public String getShortDescription() { return shortDescription; }
    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }

    public String getFullDescription() { return fullDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }

    public String getFabricComposition() { return fabricComposition; }
    public void setFabricComposition(String fabricComposition) { this.fabricComposition = fabricComposition; }

    public Integer getFabricGsm() { return fabricGsm; }
    public void setFabricGsm(Integer fabricGsm) { this.fabricGsm = fabricGsm; }

    public String getFitType() { return fitType; }
    public void setFitType(String fitType) { this.fitType = fitType; }

    public String getSleeveType() { return sleeveType; }
    public void setSleeveType(String sleeveType) { this.sleeveType = sleeveType; }

    public String getCollarOrNeck() { return collarOrNeck; }
    public void setCollarOrNeck(String collarOrNeck) { this.collarOrNeck = collarOrNeck; }

    public String getPatternOrPrint() { return patternOrPrint; }
    public void setPatternOrPrint(String patternOrPrint) { this.patternOrPrint = patternOrPrint; }

    public String getWashCare() { return washCare; }
    public void setWashCare(String washCare) { this.washCare = washCare; }

    public Integer getWeightGrams() { return weightGrams; }
    public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }

    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }

    public Boolean getIsNewArrival() { return isNewArrival; }
    public void setIsNewArrival(Boolean isNewArrival) { this.isNewArrival = isNewArrival; }

    public Boolean getIsCodEligible() { return isCodEligible; }
    public void setIsCodEligible(Boolean isCodEligible) { this.isCodEligible = isCodEligible; }

    public Boolean getIsReturnable() { return isReturnable; }
    public void setIsReturnable(Boolean isReturnable) { this.isReturnable = isReturnable; }

    public BigDecimal getAverageRating() { return averageRating; }
    public void setAverageRating(BigDecimal averageRating) { this.averageRating = averageRating; }

    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }

    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}