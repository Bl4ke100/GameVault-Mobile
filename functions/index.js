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

// Broadcast Notifications to All Users
exports.sendBroadcastNotification = onDocumentCreated(
  "broadcasts/{broadcastId}",
  async (event) => {
    const data = event.data.data();
    
    try {
      const usersSnap = await getFirestore().collection("users").get();
      const tokens = [];
      
      usersSnap.forEach((doc) => {
        const u = doc.data();
        if (u && u.fcmToken) {
          tokens.push(u.fcmToken);
        }
      });

      if (tokens.length === 0) {
        console.log("No valid FCM tokens registered in the DB.");
        await event.data.ref.update({ status: "FAILED" });
        return;
      }

      let successCount = 0;
      for (const t of tokens) {
        const message = {
          notification: {
            title: data.title || "GameVault Admin",
            body: data.body || "New alert!"
          },
          android: {
            notification: {
              channelId: "gamevault_orders",
              priority: "high"
            }
          },
          token: t
        };
        try {
          await getMessaging().send(message);
          successCount++;
        } catch (e) {
          console.error("Token fail:", e);
        }
      }

      console.log("Broadcast success! Dispatched:", successCount);
      await event.data.ref.update({
        status: "COMPLETED",
        successCount: successCount
      });

    } catch (error) {
      console.error("Fatal broadcast error:", error);
      await event.data.ref.update({
        status: "FAILED",
        error: error.toString()
      });
    }
  }
);