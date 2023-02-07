<h1>Project Name: Sensor Data Management</h1>
<p>A Java program that retrieves data from 6 sensors (3 for humidity, 3 for temperature, and 3 for light) and stores it in a MongoDB database. The program then transfers the data to an SQL database, checks for outliers, and creates alerts if necessary. The project also includes a mobile app that displays a graphic of the data and sends alerts.</p><h2>Features</h2><ul><li>Retrieve data from 6 sensors (3 for humidity, 3 for temperature, and 3 for light)</li><li>Store the data in a MongoDB database</li><li>Transfer the data to an SQL database</li><li>Check for outliers and insert them into an outlier table</li><li>Check if the read is within a safe range and insert it into a readings table</li><li>Create alerts and insert them into an alerts table</li><li>Mobile app for data visualization and alerts</li></ul><h2>Requirements</h2><ul><li>Java 8 or higher</li><li>MongoDB</li><li>SQL database (e.g. MySQL, PostgreSQL, etc.)</li><li>Mobile development environment (e.g. Android Studio, Xcode, etc.)</li></ul><h2>Getting Started</h2><ol><li>Clone the repository</li></ol><pre><div class="bg-black mb-4 rounded-md"><div class="flex items-center relative text-gray-200 bg-gray-800 px-4 py-2 text-xs font-sans"><span class="">bash</span><button class="flex ml-auto gap-2"><svg stroke="currentColor" fill="none" stroke-width="2" viewBox="0 0 24 24" stroke-linecap="round" stroke-linejoin="round" class="h-4 w-4" height="1em" width="1em" xmlns="http://www.w3.org/2000/svg"><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"></path><rect x="8" y="2" width="8" height="4" rx="1" ry="1"></rect></svg>Copy code</button></div><div class="p-4 overflow-y-auto"><code class="!whitespace-pre hljs language-bash">git <span class="hljs-built_in">clone</span> https://github.com/&lt;your-username&gt;/sensor-data-management.git
</code></div></div></pre><ol start="2"><li>Import the project into your preferred Java development environment</li><li>Configure the MongoDB and SQL database connections in the <code>config.properties</code> file</li><li>Open the mobile app project in your preferred mobile development environment</li><li>Configure the API endpoint in the mobile app to point to your Java program</li><li>Run the Java program</li><li>Build and run the mobile app on a physical device or emulator</li></ol><h2>Documentation</h2><p>The source code for both the Java program and the mobile app are well-documented and should provide enough information for you to understand how the program works. If you need further assistance, feel free to open an issue in the repository.</p><h2>Contributions</h2><p>This project is open for contributions. If you'd like to contribute, please fork the repository and make a pull request with your changes.</p><h2>License</h2><p>This project is licensed under the MIT License. Please see the <code>LICENSE</code> file for more information.</p></div>
