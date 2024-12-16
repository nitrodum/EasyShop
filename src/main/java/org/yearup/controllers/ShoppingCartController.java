package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.configurations.UserHelper;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.security.Principal;

// convert this class to a REST controller
// only logged-in users should have access to these actions
@RestController
@RequestMapping("cart")
@PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
@CrossOrigin
public class ShoppingCartController
{
    // a shopping cart requires
    private final ShoppingCartDao shoppingCartDao;
    private final ProductDao productDao;
    private final UserHelper userHelper;

    public ShoppingCartController(ShoppingCartDao shoppingCartDao, ProductDao productDao, UserHelper userHelper) {
        this.shoppingCartDao = shoppingCartDao;
        this.productDao = productDao;
        this.userHelper = userHelper;
    }

    // each method in this controller requires a Principal object as a parameter
    @GetMapping
    public ResponseEntity<ShoppingCart> getCart(Principal principal) {
        try {
            int userId = userHelper.getUserId(principal);
            ShoppingCart cart = shoppingCartDao.getByUserId(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("products/{productId}")
    public ResponseEntity<ShoppingCart> addProduct(@PathVariable int productId, Principal principal) {
        try {
            int userId = userHelper.getUserId(principal);
            ShoppingCart cart = shoppingCartDao.getByUserId(userId);

            if (cart.contains(productId)) {
                shoppingCartDao.update(userId, cart.get(productId));
            } else {
                Product product = productDao.getById(productId);
                if (product == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                shoppingCartDao.addProduct(userId, product);
            }

            ShoppingCart updatedCart = shoppingCartDao.getByUserId(userId);
            return ResponseEntity.ok(updatedCart);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("products/{productId}")
    public ResponseEntity<Void> updateItem(@PathVariable int productId, @RequestBody ShoppingCartItem item, Principal principal) {
        try {
            int userId = userHelper.getUserId(principal);
            Product p = new Product();
            p.setProductId(productId);
            item.setProduct(p);
            shoppingCartDao.update(userId, item);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping
    public ResponseEntity<ShoppingCart> deleteCart(Principal principal) {
        try {
            int userId = userHelper.getUserId(principal);
            shoppingCartDao.delete(userId);
            return ResponseEntity.ok(shoppingCartDao.getByUserId(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}


