VintageVinyl is a desktop application for managing personal vinyl record collections. 
With an embedded SQLite database and Discogs connectivity, the application allows users to: 
- catalog their personal record collection
- track projected market values
- research and maintain an album wishlist

VintageVinyl connects to the Discogs API using OAuth 1.0a authorization via the user's personal Discogs account.

IMPORTANT: A Discogs account with seller settings configured is required for VintageVinyl to function properly. Seller accounts provide access to Discogs recommended pricing.

VintageVinyl's accompanying server application, VVServer, is currently running on a private cloud VM configured for https. This server acts as a proxy server between the VintageVinyl client and the Discogs API: 
- Client applications never interact with the consumer key and secret.
- All users' private keys and secrets are stored seurely on the client using the java-keyring api.

* Link to server repo: https://github.com/alec-shell/VVServer/tree/main

<img width="999" height="603" alt="Screenshot 2026-04-26 at 2 50 29 PM" src="https://github.com/user-attachments/assets/45b4a14d-6fb2-4ec0-ab0f-6a3ff766b6af" />
<img width="1000" height="600" alt="Screenshot 2026-04-26 at 2 51 32 PM" src="https://github.com/user-attachments/assets/25c4e444-0fdb-4ff5-b8ab-35b1a3957177" />
<img width="1002" height="600" alt="Screenshot 2026-04-26 at 2 51 43 PM" src="https://github.com/user-attachments/assets/a196a422-3a46-4910-bf9d-b6207d6257ea" />



