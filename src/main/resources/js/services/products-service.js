let productService;

class LRUCache {
    constructor(maxSize) {
        this.maxSize = maxSize;
        this.cache = new Map();
    }

    get(key) {
        if (!this.cache.has(key)) return null;

        const value = this.cache.get(key);

        this.cache.delete(key);
        this.cache.set(key, value);
        return value;
    }

    set(key, value) {
        if (this.cache.has(key)) {
            this.cache.delete(key);
        }

        if (this.cache.size == this.maxSize) {
            const firstKey = this.cache.keys().next().value;
            //console.log("Key Deleted: ", firstKey)
            this.cache.delete(firstKey);
        }

         this.cache.set(key, value);
         //console.log("cache size: ", this.cache.size)

    }
}

class ProductService {

    photos = [];
    cache = new LRUCache(10);

    filter = {
        cat: undefined,
        minPrice: undefined,
        maxPrice: undefined,
        color: undefined,
        page: 1,
        pageSize: 9,
        queryString: () => {
            let qs = "";
            if(this.filter.cat){ qs = `cat=${this.filter.cat}`; }
            if(this.filter.minPrice)
            {
                const minP = `minPrice=${this.filter.minPrice}`;
                if(qs.length>0) {   qs += `&${minP}`; }
                else { qs = minP; }
            }
            if(this.filter.maxPrice)
            {
                const maxP = `maxPrice=${this.filter.maxPrice}`;
                if(qs.length>0) {   qs += `&${maxP}`; }
                else { qs = maxP; }
            }
            if(this.filter.color)
            {
                const col = `color=${this.filter.color}`;
                if(qs.length>0) {   qs += `&${col}`; }
                else { qs = col; }
            }
            qs += (qs.length ? "&" : "") + `page=${this.filter.page}&pageSize=${this.filter.pageSize}`;

            return qs.length > 0 ? `?${qs}` : "";
        }
    }

    constructor() {

        //load list of photos into memory
        axios.get("/images/products/photos.json")
            .then(response => {
                this.photos = response.data;
            });


    }

    hasPhoto(photo){
        return this.photos.filter(p => p == photo).length > 0;
    }

    addCategoryFilter(cat)
    {
        if(cat == 0) this.clearCategoryFilter();
        else this.filter.cat = cat;
    }
    addMinPriceFilter(price)
    {
        if(price == 0 || price == "") this.clearMinPriceFilter();
        else this.filter.minPrice = price;
    }
    addMaxPriceFilter(price)
    {
        if(price == 0 || price == "") this.clearMaxPriceFilter();
        else this.filter.maxPrice = price;
    }
    addColorFilter(color)
    {
        if(color == "") this.clearColorFilter();
        else this.filter.color = color;
    }

    clearCategoryFilter()
    {
        this.filter.cat = undefined;
    }
    clearMinPriceFilter()
    {
        this.filter.minPrice = undefined;
    }
    clearMaxPriceFilter()
    {
        this.filter.maxPrice = undefined;
    }
    clearColorFilter()
    {
        this.filter.color = undefined;
    }

    search()
    {
        const queryKey = this.filter.queryString();
        const url = `${config.baseUrl}/products${queryKey}`;

        const cachedData = this.cache.get(queryKey);
        if(cachedData) {
            //console.log("Cached data retrieved", queryKey);
            this.renderProducts(cachedData);
            this.prefetchAdjacentPages();
            return;
        }

        axios.get(url)
             .then(response => {
                 let data = {};
                 data.products = response.data;

                 data.products.forEach(product => {
                     if(!this.hasPhoto(product.imageUrl))
                     {
                         product.imageUrl = "no-image.jpg";
                     }
                 })

                this.cache.set(queryKey, data);
                //console.log("Caching data", queryKey);
                this.renderProducts(data);
                this.prefetchAdjacentPages();

             })
            .catch(error => {

                const data = {
                    error: "Searching products failed."
                };

                templateBuilder.append("error", data, "errors")
            });
    }

    renderProducts(data) {
        templateBuilder.build('product', data, 'content', this.enableButtons, this);
    }

    prefetchAdjacentPages() {
        const currentPage = this.filter.page;

        const pagesToPrefetch = [
            { page: currentPage - 1, key: this.getPageKey(currentPage - 1)},
            { page: currentPage + 1, key: this.getPageKey(currentPage + 1)}
        ];

        pagesToPrefetch.forEach(({ page, key }) => {
        //console.log(page, key);

            if (page > 0 && !this.cache.get(key)) {
                const url = `${config.baseUrl}/products${key}`;
                //console.log(`Prefetching page: ${page}`);

                axios.get(url)
                     .then(response => {
                         let data = {};
                         data.products = response.data;

                         data.products.forEach(product => {
                             if(!this.hasPhoto(product.imageUrl))
                             {
                                 product.imageUrl = "no-image.jpg";
                             }
                         })

                        this.cache.set(key, data);

                     })
                    .catch(error => {

                        const data = {
                            error: "Searching products failed."
                        };

                        templateBuilder.append("error", data, "errors")
                    });
            }
        });
    }

    updatePagination() {
        const currentPage = this.filter.page;

        const paginationData = {
            page: currentPage,
            prevDisabled: currentPage === 1,
        };

        templateBuilder.appendWithCallback('pagination', paginationData, 'content', () => {
            this.addPaginationEventListeners();
        });
    }


    addPaginationEventListeners() {
        document.getElementById("next-page").addEventListener("click", () => {
            const currentPage = this.filter.page;
            this.setPage(currentPage + 1);
        });

        document.getElementById("prev-page").addEventListener("click", () => {
            const currentPage = this.filter.page;
            this.setPage(currentPage - 1);
        });
    }

    enableButtons()
    {
        const buttons = [...document.querySelectorAll(".add-button")];

        if(userService.isLoggedIn())
        {
            buttons.forEach(button => {
                button.classList.remove("invisible")
            });
        }
        else
        {
            buttons.forEach(button => {
                button.classList.add("invisible")
            });
        }
    }

    setPage(pageNumber) {
        this.filter.page = pageNumber;
        this.search();
    }

    setPageSize(size) {
        this.filter.pageSize = size;
        this.filter.page = 1;
        this.search();
    }

    getPageKey(page) {
        const originalPage = this.filter.page;
        this.filter.page = page;
        const key = this.filter.queryString();
        this.filter.page = originalPage;
        return key;
    }

}


document.addEventListener('DOMContentLoaded', () => {
    productService = new ProductService();

});
