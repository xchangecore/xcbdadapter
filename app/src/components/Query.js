import React from "react";
import { Component } from "react";
import axios from "axios";
import "./FileUpload.css";
import SelectBox from "../features/select-box";

class Query extends Component {

    constructor(props) {
        super(props);
        this.state = {
            configurationName: "cvs",
            listConfigurationName: []
        };
        this.getConfigurationName();
    }

    onSelectConfiguration(name) {
        this.setState({ configurationName: name });
    }

    getConfigurationName() {
        axios.get("/api/listConfigurationName").then(res => {
            var list = [];
            for (var i = 0; i < res.data.length; i++) {
                console.log("Configuration: ", res.data[i]);
                list[i] = {
                    value: res.data[i],
                    id: i + 1
                };
            }
            //console.log("listConfigurationName: ", list);
            this.setState({ listConfigurationName: list });
        });
    }

    render() {
        return (
            <div className='container'>
                <div className='row'>
                    <div className='col-md-6'>
                        <div>
                            {this.state.listConfigurationName.length === 0 ? null : (
                                <div style={{ margin: "16px", position: "relative" }}>
                                    <h3>Configuration Name</h3>
                                    <SelectBox items={this.state.listConfigurationName} />
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default Query;