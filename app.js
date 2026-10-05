const payButton = document.getElementById("payButton");
const statusElement = document.getElementById("status");

payButton.addEventListener("click", async () => {

    const token = document.getElementById("token").value.trim();

    const productId = Number(
        document.getElementById("productId").value
    );

    const quantity = Number(
        document.getElementById("quantity").value
    );

    if (!token) {
        statusElement.textContent = "Please enter your JWT token.";
        return;
    }

    if (!productId || quantity < 1) {
        statusElement.textContent =
            "Please enter a valid product and quantity.";
        return;
    }

    statusElement.textContent = "Creating order...";

    try {

        // 1. Create our application order
        const response = await fetch(
            "http://localhost:8088/orders/create",
            {
                method: "POST",

                headers: {
                    "Content-Type": "application/json",
                    "Authorization": token.startsWith("Bearer ")
                        ? token
                        : `Bearer ${token}`
                },

                body: JSON.stringify({
                    items: [
                        {
                            productId: productId,
                            quantity: quantity
                        }
                    ]
                })
            }
        );

        const data = await response.json();

        if (!response.ok) {
            throw new Error(
                data.message || `HTTP ${response.status}`
            );
        }

        console.log("Order response:", data);

        /*
         * data now contains:
         *
         * orderId
         * paymentId
         * providerOrderId
         * amount
         * currency
         * status
         */

        statusElement.textContent =
            `Order ${data.orderId} created. Opening payment...`;

        // 2. Configure Razorpay Checkout

        const options = {

            key: "rzp_test_TenJBv0AXrifb1",

            amount: data.amount,

            currency: data.currency,

            name: "Nidhi E-Commerce",

            description: `Order #${data.orderId}`,

            order_id: data.providerOrderId,

                handler: async function (paymentResponse) {

                    console.log(
                        "Razorpay payment response:",
                        paymentResponse
                    );

                    statusElement.textContent =
                        "Payment completed. Confirming order...";

                    try {

                        const reconcileResponse = await fetch(
                            `http://localhost:8088/orders/${data.orderId}/reconcile`,
                            {
                                method: "GET",

                                headers: {
                                    "Authorization": token.startsWith("Bearer ")
                                        ? token
                                        : `Bearer ${token}`
                                }
                            }
                        );

                        const reconcileData = await reconcileResponse.json();

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

                        if (reconcileData.status === "SUCCESS") {
                            statusElement.textContent =
                                `Payment successful. Order ${data.orderId} is confirmed.`;
                        } else if (reconcileData.status === "FAILED") {
                            statusElement.textContent =
                                `Payment failed. Order ${data.orderId} was not confirmed.`;
                        } else {
                            statusElement.textContent =
                                `Payment status: ${reconcileData.status}`;
                        }

                    } catch (error) {

                        console.error(
                            "Reconciliation error:",
                            error
                        );

                        statusElement.textContent =
                            `Payment completed, but confirmation failed: ${error.message}`;
                    }
                },

            modal: {
                ondismiss: function () {

                    statusElement.textContent =
                        "Payment window closed.";
                }
            }
        };

        // 3. Create Razorpay Checkout instance

        const razorpay = new Razorpay(options);

        // 4. Open Razorpay Checkout

        razorpay.open();

    } catch (error) {

        console.error(error);

        statusElement.textContent =
            `Error: ${error.message}`;
    }
});