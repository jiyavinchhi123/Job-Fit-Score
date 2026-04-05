const API_BASE = (window.API_BASE || "https://jobfit-backend-gnr1.onrender.com");


function msg(text, cls = "muted") {
    const el = document.getElementById("paymentMsg");
    el.className = cls;
    el.textContent = text;
}

document.getElementById("upgradeBtn").addEventListener("click", async () => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
        msg("Please login/register before upgrading.", "error");
        return;
    }

    try {
        msg("Creating payment order...");
        const createRes = await fetch(`${API_BASE}/create-payment`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userId: Number(userId), amount: 19900 })
        });
        const order = await createRes.json();
        if (!createRes.ok) throw new Error(order.error || "Payment creation failed");

        const options = {
            key: order.key,
            amount: order.amount,
            currency: order.currency,
            name: order.name,
            description: order.description,
            order_id: order.orderId,
            handler: async function (response) {
                await fetch(`${API_BASE}/payment-webhook`, {
                    method: "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({
                        userId: Number(userId),
                        razorpayPaymentId: response.razorpay_payment_id,
                        status: "success"
                    })
                });
                localStorage.setItem("plan", "pro");
                msg("Payment successful. Pro plan activated.", "success");
            },
            modal: {
                ondismiss: async function () {
                    await fetch(`${API_BASE}/payment-webhook`, {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({
                            userId: Number(userId),
                            razorpayPaymentId: null,
                            status: "failure"
                        })
                    });
                    msg("Payment cancelled.", "error");
                }
            },
            theme: { color: "#4F46E5" }
        };

        const rzp = new Razorpay(options);
        rzp.open();
    } catch (e) {
        msg(e.message || "Payment failed", "error");
    }
});
