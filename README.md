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

<img width="1440" height="900" alt="Screenshot 2026-02-23 at 6 10 17 PM" src="https://github.com/user-attachments/assets/13d878f2-9f36-4179-8308-1443a46d6176" />
<img width="1440" height="900" alt="Screenshot 2026-02-23 at 6 11 33 PM" src="https://github.com/user-attachments/assets/ce7a5996-a800-404b-b6d5-0e322667ea50" />
<img width="1440" height="900" alt="Screenshot 2026-02-23 at 6 12 09 PM" src="https://github.com/user-attachments/assets/aad54ac4-82c7-4ec6-b728-1f717374909a" />
<img width="1440" height="900" alt="Screenshot 2026-02-24 at 8 31 43 PM" src="https://github.com/user-attachments/assets/d19fbff7-83d2-49cd-a628-d19aed76539c" />
