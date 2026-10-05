const API_BASE_URL = "http://localhost:8088";

let products = [];
let cart = [];

let currentPage = 0;
let totalPages = 1;

// ============================================================
// ELEMENTS
// ============================================================

const homePage = document.getElementById("homePage");
const productsPage = document.getElementById("productsPage");
const productDetailsPage = document.getElementById("productDetailsPage");
const cartPage = document.getElementById("cartPage");
const successPage = document.getElementById("successPage");
const ordersPage = document.getElementById("ordersPage");
const failurePage = document.getElementById("failurePage");
const loginButton = document.getElementById("loginButton");

const featuredProducts = document.getElementById("featuredProducts");
const productsGrid = document.getElementById("productsGrid");
const productDetails = document.getElementById("productDetails");
const cartContent = document.getElementById("cartContent");
const cartCount = document.getElementById("cartCount");


// ============================================================
// NAVIGATION
// ============================================================

function hideAllPages() {


homePage.classList.add("hidden");
productsPage.classList.add("hidden");
productDetailsPage.classList.add("hidden");
cartPage.classList.add("hidden");
successPage.classList.add("hidden");
failurePage.classList.add("hidden");


}

function showPage(page) {


hideAllPages();

page.classList.remove("hidden");

window.scrollTo({
    top: 0,
    behavior: "smooth"
});


}

document.getElementById("homeLink").addEventListener("click", function (event) {


event.preventDefault();

showPage(homePage);


});

document.getElementById("productsLink").addEventListener("click", function (event) {


event.preventDefault();

showPage(productsPage);

loadProducts();


});

document.getElementById("shopNowButton").addEventListener("click", function () {


showPage(productsPage);

loadProducts();


});

document.getElementById("viewAllButton").addEventListener("click", function () {


showPage(productsPage);

loadProducts();


});

document.getElementById("cartLink").addEventListener("click", function (event) {


event.preventDefault();

showPage(cartPage);

renderCart();


});

document.getElementById("backToProducts").addEventListener("click", function () {


showPage(productsPage);


});

document.getElementById("continueShoppingButton")
    .addEventListener("click", function () {

        showPage(productsPage);

        loadProducts();

    });

document.getElementById("ordersLink")
    .addEventListener("click", function (event) {

        event.preventDefault();

        const token =
            sessionStorage.getItem("access_token");

        if (!token) {
            alert("Please login to view your orders.");
            startLogin();
            return;
        }

        showPage(ordersPage);

        loadOrders();
    });
// ============================================================
// PRODUCTS
// ============================================================

async function loadProducts(page = 0) {
    try {
        productsGrid.innerHTML = `
            <div class="loading">
                Loading products...
            </div>
        `;

        const response = await fetch(
            `${API_BASE_URL}/products?page=${page}`
        );

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const data = await response.json();

        products = data.content || [];

        currentPage = data.number;
        totalPages = data.totalPages;

        renderProducts();
        updatePagination();

    } catch (error) {
        console.error("Failed to load products:", error);

        productsGrid.innerHTML = `
            <p>Unable to load products.<br>
            HTTP ${error.message.replace("HTTP ", "")}</p>
        `;
    }
}

function updatePagination() {
    const pageInfo = document.getElementById("pageInfo");
    const previousButton = document.getElementById("previousPage");
    const nextButton = document.getElementById("nextPage");

    pageInfo.textContent = `Page ${currentPage + 1} of ${totalPages}`;

    previousButton.disabled = currentPage === 0;
    nextButton.disabled = currentPage >= totalPages - 1;
}

document.getElementById("previousPage").addEventListener("click", () => {
    if (currentPage > 0) {
        loadProducts(currentPage - 1);
    }
});

document.getElementById("nextPage").addEventListener("click", () => {
    if (currentPage < totalPages - 1) {
        loadProducts(currentPage + 1);
    }
});

// ============================================================
// FEATURED PRODUCTS
// ============================================================

function renderFeaturedProducts() {


if (!products.length) {

    featuredProducts.innerHTML = `
        <div class="loading">
            No products available.
        </div>
    `;

    return;
}

const featured = products.slice(0, 6);

featuredProducts.innerHTML =
    featured.map(product => createProductCard(product)).join("");


}

// ============================================================
// ALL PRODUCTS
// ============================================================

function renderProducts() {


if (!products.length) {

    productsGrid.innerHTML = `
        <div class="loading">
            No products available.
        </div>
    `;

    return;
}

productsGrid.innerHTML =
    products.map(product => createProductCard(product)).join("");


}

// ============================================================
// PRODUCT CARD
// ============================================================

function createProductCard(product) {


const imageUrl = cleanImageUrl(product.imageUrl);

return `
    <div class="product-card">

        <div
            class="product-image"
            onclick="showProductDetails(${product.id})"
            style="cursor: pointer;"
        >

            ${
    imageUrl
        ? `
                        <img
                            src="${imageUrl}"
                            alt="${escapeHtml(product.title)}"
                            onerror="this.style.display='none'; this.parentElement.innerHTML='<span>Product Image</span>';"
                            style="
                                width: 100%;
                                height: 100%;
                                object-fit: cover;
                            "
                        >
                      `
        : `
                        <span>Product Image</span>
                      `
}

        </div>


        <div class="product-info">

            <div class="product-category">
                ${escapeHtml(product.category || "Product")}
            </div>

            <h3 class="product-name">
                ${escapeHtml(product.title)}
            </h3>

            <p class="product-description">
                ${escapeHtml(product.description || "")}
            </p>


            <div class="product-bottom">

                <div class="product-price">
                    ₹${formatPrice(product.price)}
                </div>

                <button
                    class="add-button"
                    onclick="addToCart(${product.id})"
                >
                    Add to Cart
                </button>

            </div>

        </div>

    </div>
`;


}

// ============================================================
// PRODUCT DETAILS
// ============================================================

window.showProductDetails = function (productId) {


const product = products.find(
    product => product.id === productId
);

if (!product) {
    return;
}

const imageUrl = cleanImageUrl(product.imageUrl);

productDetails.innerHTML = `

    <div style="
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 50px;
        align-items: center;
    ">

        <div
            style="
                height: 400px;
                background: #f0f0ed;
                border-radius: 12px;
                display: flex;
                align-items: center;
                justify-content: center;
                overflow: hidden;
            "
        >

            ${
    imageUrl
        ? `
                        <img
                            src="${imageUrl}"
                            alt="${escapeHtml(product.title)}"
                            onerror="this.style.display='none'; this.parentElement.innerHTML='<span>Product Image</span>';"
                            style="
                                width: 100%;
                                height: 100%;
                                object-fit: cover;
                            "
                        />
                      `
        : `
                        <span>Product Image</span>
                      `
}

        </div>


        <div>

            <div class="product-category">
                ${escapeHtml(product.category || "Product")}
            </div>

            <h1>
                ${escapeHtml(product.title)}
            </h1>

            <h2>
                ₹${formatPrice(product.price)}
            </h2>

            <p style="
                color: #777;
                line-height: 1.7;
                margin: 25px 0;
            ">
                ${escapeHtml(product.description || "")}
            </p>


            <button
                class="primary-button"
                onclick="addToCart(${product.id})"
            >
                Add to Cart
            </button>

        </div>

    </div>
`;

showPage(productDetailsPage);


};

// ============================================================
// CART
// ============================================================

window.addToCart = function (productId) {


const product = products.find(
    product => product.id === productId
);

if (!product) {
    return;
}

const existingItem = cart.find(
    item => item.product.id === productId
);

if (existingItem) {

    existingItem.quantity++;

} else {

    cart.push({
        product: product,
        quantity: 1
    });
}

updateCartCount();

console.log("Cart:", cart);


};

function updateCartCount() {


const count = cart.reduce(
    (total, item) => total + item.quantity,
    0
);

cartCount.textContent = count;


}

function renderCart() {


if (!cart.length) {

    cartContent.innerHTML = `

        <div class="result-card">

            <h2>
                Your cart is empty
            </h2>

            <p>
                Browse our products and add something
                you like to your cart.
            </p>

            <button
                class="primary-button"
                onclick="goToProducts()"
            >
                Browse Products
            </button>

        </div>

    `;

    return;
}


let total = 0;

const itemsHtml = cart.map(item => {

    const itemTotal =
        item.product.price * item.quantity;

    total += itemTotal;

    return `

        <div class="cart-item">

            <div>

                <strong>
                    ${escapeHtml(item.product.title)}
                </strong>

                <p>
                    ₹${formatPrice(item.product.price)}
                    × ${item.quantity}
                </p>

            </div>


            <strong>
                ₹${formatPrice(itemTotal)}
            </strong>

        </div>

    `;

}).join("");


cartContent.innerHTML = `

    <div class="cart-container">

        <div class="cart-items">

            ${itemsHtml}

        </div>


        <div class="cart-summary">

            <h2>
                Order Summary
            </h2>

            <div style="
                display: flex;
                justify-content: space-between;
                margin: 25px 0;
            ">

                <span>
                    Total
                </span>

                <strong>
                    ₹${formatPrice(total)}
                </strong>

            </div>


            <button
                class="primary-button"
                style="width: 100%;"
                onclick="checkoutCart()"
            >
                Checkout
            </button>

        </div>

    </div>

`;


}

window.goToProducts = function () {


showPage(productsPage);

loadProducts();


};

// ============================================================
// CHECKOUT
// ============================================================

window.checkoutCart = function () {

    if (!cart.length) {
        return;
    }

    const token = sessionStorage.getItem("access_token");

    if (!token) {
        alert("Please login before placing an order.");
        startLogin();
        return;
    }

    createOrderAndPay();
};

// ============================================================
// CREATE ORDER + RAZORPAY
// ============================================================

async function createOrderAndPay() {


const statusElement = document.getElementById("status");

    const token = sessionStorage.getItem("access_token");

    if (!token) {
        alert("Please login before placing an order.");
        startLogin();
        return;
    }

try {

    const response = await fetch(
        `${API_BASE_URL}/orders/create`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                items: cart.map(item => ({
                    productId: item.product.id,
                    quantity: item.quantity
                }))
            })
        }
    );


    const data = await response.json();


    if (!response.ok) {

        throw new Error(
            data.message ||
            `HTTP ${response.status}`
        );
    }


    console.log(
        "Order response:",
        data
    );


    const options = {

        key: "rzp_test_TenJBv0AXrifb1",

        amount: data.amount,

        currency: data.currency,

        name: "Nidhi E-Commerce",

        description:
            `Order #${data.orderId}`,

        order_id:
        data.providerOrderId,


        handler:
            async function (paymentResponse) {

                console.log(
                    "Razorpay payment response:",
                    paymentResponse
                );


                try {

                    const reconcileResponse =
                        await fetch(
                            `${API_BASE_URL}/orders/${data.orderId}/reconcile`,
                            {
                                method: "GET",

                                headers: {
                                    "Authorization":
                                        token.startsWith("Bearer ")
                                            ? token
                                            : `Bearer ${token}`
                                }
                            }
                        );


                    const reconcileData =
                        await reconcileResponse.json();


                    if (!reconcileResponse.ok) {

                        throw new Error(
                            reconcileData.message ||
                            `HTTP ${reconcileResponse.status}`
                        );
                    }


                    console.log(
                        "Reconciliation response:",
                        reconcileData
                    );


                    if (
                        reconcileData.status ===
                        "SUCCESS"
                    ) {

                        cart = [];

                        updateCartCount();

                        showSuccessPage(
                            data.orderId,
                            data.amount
                        );

                    } else if (
                        reconcileData.status ===
                        "FAILED"
                    ) {

                        showFailurePage(
                            data.orderId
                        );

                    } else {

                        alert(
                            `Payment status: ${reconcileData.status}`
                        );
                    }


                } catch (error) {

                    console.error(
                        "Reconciliation error:",
                        error
                    );

                    alert(
                        `Payment completed, but confirmation failed: ${error.message}`
                    );
                }
            },


        modal: {

            ondismiss:
                function () {

                    console.log(
                        "Payment window closed."
                    );
                }
        }
    };


    const razorpay =
        new Razorpay(options);


    razorpay.open();


} catch (error) {

    console.error(error);

    alert(
        `Error: ${error.message}`
    );
}


}

// ============================================================
// SUCCESS / FAILURE
// ============================================================

function showSuccessPage(orderId, amount) {


document.getElementById(
    "successOrderId"
).textContent = orderId;


document.getElementById(
    "successAmount"
).textContent =
    `₹${formatPrice(amount / 100)}`;


document.getElementById(
    "successMessage"
).textContent =
    `Your payment was completed and Order #${orderId} has been confirmed.`;


showPage(successPage);


}

function showFailurePage(orderId) {


document.getElementById(
    "failureMessage"
).textContent =
    `Payment for Order #${orderId} could not be confirmed.`;


showPage(failurePage);


}

document.getElementById("tryAgainButton")
.addEventListener("click", function () {


showPage(cartPage);

renderCart();
});


// ============================================================
// LOGIN
// ============================================================

loginButton.addEventListener("click", () => {

    const token = sessionStorage.getItem("access_token");

    if (token) {
        logout();
    } else {
        startLogin();
    }

});


function getUserIdFromToken() {

    const token =
        sessionStorage.getItem("access_token");

    if (!token) {
        return null;
    }

    try {

        const payload = JSON.parse(
            atob(
                token
                    .split(".")[1]
                    .replace(/-/g, "+")
                    .replace(/_/g, "/")
            )
        );

        return payload.userId || null;

    } catch (error) {

        console.error(
            "Failed to decode access token:",
            error
        );

        return null;
    }
}

async function loadOrders() {

    const ordersContent =
        document.getElementById("ordersContent");

    const token =
        sessionStorage.getItem("access_token");

    if (!token) {

        ordersContent.innerHTML = `
            <div class="result-card">
                <h2>Please login</h2>
                <p>
                    Login to view your orders.
                </p>
            </div>
        `;

        return;
    }

    const userId =
        getUserIdFromToken();

    if (!userId) {

        ordersContent.innerHTML = `
            <div class="result-card">
                <h2>Unable to identify user</h2>
                <p>
                    Please login again.
                </p>
            </div>
        `;

        return;
    }

    try {

        ordersContent.innerHTML = `
            <div class="loading">
                Loading your orders...
            </div>
        `;

        const response = await fetch(
            `${API_BASE_URL}/orders/get-for-user/${userId}`,
            {
                method: "GET",

                headers: {
                    "Authorization":
                        `Bearer ${token}`
                }
            }
        );

        if (!response.ok) {

            throw new Error(
                `HTTP ${response.status}`
            );
        }

        const orders =
            await response.json();

        renderOrders(orders);

    } catch (error) {

        console.error(
            "Failed to load orders:",
            error
        );

        ordersContent.innerHTML = `
            <div class="result-card">
                <h2>Unable to load orders</h2>
                <p>
                    ${escapeHtml(error.message)}
                </p>
            </div>
        `;
    }
}

function renderOrders(orders) {

    const ordersContent =
        document.getElementById("ordersContent");

    if (!orders.length) {

        ordersContent.innerHTML = `
            <div class="result-card">
                <h2>No orders yet</h2>

                <p>
                    You haven't placed any orders yet.
                </p>

                <button
                    class="primary-button"
                    onclick="goToProducts()"
                >
                    Browse Products
                </button>
            </div>
        `;

        return;
    }

    ordersContent.innerHTML = orders
        .map(order => {

            const items =
                order.items
                    .map(item => `
                        <div class="order-item">
                            <span>
                                Product #${item.productId}
                            </span>

                            <span>
                                × ${item.quantity}
                            </span>
                        </div>
                    `)
                    .join("");

            return `
                <div class="order-card">

                    <div class="order-header">

                        <div>
                            <p class="eyebrow">
                                ORDER
                            </p>

                            <h3>
                                #${order.id}
                            </h3>
                        </div>

                        <div class="order-status ${order.status.toLowerCase()}">
                            ${order.status}
                        </div>

                    </div>

                    <div class="order-items">

                        ${items}

                    </div>

                    <div class="order-footer">

                        <span>
                            Total
                        </span>

                        <strong>
                            ₹${formatPrice(order.amount)}
                        </strong>

                    </div>

                </div>
            `;

        })
        .join("");
}

async function startLogin() {
    const codeVerifier = generateCodeVerifier();

    sessionStorage.setItem("oauth_code_verifier", codeVerifier);

    const codeChallenge = await generateCodeChallenge(codeVerifier);

    const authorizationUrl =
        "http://localhost:8081/oauth2/authorize" +
        "?response_type=code" +
        "&client_id=store-web-client" +
        "&scope=openid%20profile%20product.read" +
        "&redirect_uri=" +
        encodeURIComponent(
            "http://localhost:63342/e-commerce-micorservices/index.html"
        ) +
        "&code_challenge=" +
        encodeURIComponent(codeChallenge) +
        "&code_challenge_method=S256";

    window.location.href = authorizationUrl;
}

function generateCodeVerifier() {
    const array = new Uint8Array(32);
    crypto.getRandomValues(array);

    return base64UrlEncode(array);
}

async function generateCodeChallenge(codeVerifier) {
    const encoder = new TextEncoder();

    const data = encoder.encode(codeVerifier);

    const digest = await crypto.subtle.digest(
        "SHA-256",
        data
    );

    return base64UrlEncode(new Uint8Array(digest));
}

function base64UrlEncode(bytes) {
    let binary = "";

    bytes.forEach(byte => {
        binary += String.fromCharCode(byte);
    });

    return btoa(binary)
        .replace(/\+/g, "-")
        .replace(/\//g, "_")
        .replace(/=+$/, "");
}


// ============================================================
// HELPERS
// ============================================================

function formatPrice(price) {


return Number(price).toLocaleString(
    "en-IN",
    {
        minimumFractionDigits: 0,
        maximumFractionDigits: 2
    }
);


}

function cleanImageUrl(imageUrl) {


if (!imageUrl) {
    return null;
}

/*
 * Your current database values appear to contain
 * Markdown links:
 *
 * [https://example.com/image.jpg](https://example.com/image.jpg)
 *
 * Extract the actual URL.
 */

const markdownMatch =
    imageUrl.match(
        /\((https?:\/\/[^)]+)\)/
    );

if (markdownMatch) {
    return markdownMatch[1];
}

return imageUrl;


}

function escapeHtml(value) {


if (value === null || value === undefined) {
    return "";
}

return String(value)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#039;");


}

// ============================================================
// INITIAL LOAD
// ============================================================

async function initializeApp() {

    await handleOAuthCallback();

    updateLoginButton();

    loadProducts();
}

async function handleOAuthCallback() {

    const params = new URLSearchParams(
        window.location.search
    );

    const code = params.get("code");

    // Normal page load — no OAuth callback
    if (!code) {
        return;
    }

    const codeVerifier =
        sessionStorage.getItem("oauth_code_verifier");

    if (!codeVerifier) {

        console.error(
            "PKCE code verifier not found."
        );

        return;
    }

    try {

        console.log(
            "OAuth callback detected. Exchanging code for token..."
        );

        const response = await fetch(
            "http://localhost:8081/oauth2/token",
            {
                method: "POST",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded"
                },

                body: new URLSearchParams({
                    grant_type:
                        "authorization_code",

                    client_id:
                        "store-web-client",

                    redirect_uri:
                        "http://localhost:63342/e-commerce-micorservices/index.html",

                    code:
                    code,

                    code_verifier:
                    codeVerifier
                })
            }
        );

        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(
                `Token request failed: ${response.status} ${errorText}`
            );
        }

        const tokenData =
            await response.json();

        console.log(
            "Token received:",
            tokenData
        );

        sessionStorage.setItem(
            "access_token",
            tokenData.access_token
        );

        if (tokenData.refresh_token) {

            sessionStorage.setItem(
                "refresh_token",
                tokenData.refresh_token
            );
        }

        sessionStorage.removeItem(
            "oauth_code_verifier"
        );

        // Remove ?code=... from URL
        window.history.replaceState(
            {},
            document.title,
            window.location.pathname
        );

        console.log(
            "Access token stored successfully."
        );

    } catch (error) {

        console.error(
            "OAuth login failed:",
            error
        );
    }
}

function updateLoginButton() {

    const token =
        sessionStorage.getItem("access_token");

    console.log(
        "Updating login button. Token exists:",
        !!token
    );

    if (token) {

        loginButton.textContent = "Logout";

        loginButton.classList.add(
            "logout-button"
        );

    } else {

        loginButton.textContent = "Login";

        loginButton.classList.remove(
            "logout-button"
        );
    }
}

function logout() {

    console.log("Logging out...");

    sessionStorage.removeItem(
        "access_token"
    );

    sessionStorage.removeItem(
        "refresh_token"
    );

    sessionStorage.removeItem(
        "oauth_code_verifier"
    );

    updateLoginButton();

    console.log(
        "Logged out successfully."
    );
}

initializeApp();


function hideAllPages() {

    homePage.classList.add("hidden");
    productsPage.classList.add("hidden");
    productDetailsPage.classList.add("hidden");
    cartPage.classList.add("hidden");
    ordersPage.classList.add("hidden");
    successPage.classList.add("hidden");
    failurePage.classList.add("hidden");
}


