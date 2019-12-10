import React from "react";
import Tabs from "./features/tab/Tabs";
import FileUpload from "./components/FileUpload";
import Query from "./components/Query";
import './features/tab/Tab.css'

function App() {
  return (
    <div>
      <h1>Saber Adapter</h1>
      <Tabs>
        <div label="Configuration File Upload">
          <FileUpload />
        </div>
        <div label="CSV File Upload">
          <FileUpload type="csv" />
        </div>
        <div label="Query">
          Under Construction
        </div>
      </Tabs>
    </div>
  );
}

export default App;
