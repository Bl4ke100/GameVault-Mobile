const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");
const {getMessaging} = require("firebase-admin/messaging");

initializeApp();

// We watch the top-level "orders" collection
exports.sendOrderNotification = onDocumentCreated("orders/{orderId}", async (event) => {
    
    // This is the actual data from your screenshot
    const orderData = event.data.data();
    
    // We grab the "userId" field from INSIDE the document
    const userId = orderData.userId; 

    try {
        // 1. Look up the user's "fcmToken" using that userId
        const userDoc = await getFirestore().collection("users").doc(userId).get();
        const userData = userDoc.data();

        if (!userData || !userData.fcmToken) {
            console.log("No FCM token found for user:", userId);
            return;
        }

        const fcmToken = userData.fcmToken;

        const message = {
            notification: {
                title: "Order Successful! 🎮",
                body: `Your payment for LKR ${orderData.totalAmount} was successful. Check your library!`
            },
            android: {
                notification: {
                    channelId: "gamevault_orders", // MUST match your Java code exactly
                    priority: "high"
                }
            },
            token: fcmToken
        };

        // 3. Send it to their phone
        const response = await getMessaging().send(message);
        console.log("Successfully sent message:", response);

    } catch (error) {
        console.error("Error sending message:", error);
    }
});