const {onDocumentCreated} = require("firebase-functions/v2/firestore");
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");
const {getMessaging} = require("firebase-admin/messaging");

initializeApp();

exports.sendOrderNotification = onDocumentCreated("orders/{orderId}", async (event) => {
    
    const orderData = event.data.data();
    
    const userId = orderData.userId; 

    try {
        
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
                    channelId: "gamevault_orders", 
                    priority: "high"
                }
            },
            token: fcmToken
        };

        const response = await getMessaging().send(message);
        console.log("Successfully sent message:", response);

    } catch (error) {
        console.error("Error sending message:", error);
    }
});

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

      const uniqueTokens = [...new Set(tokens)];

      let successCount = 0;
      for (const t of uniqueTokens) {
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