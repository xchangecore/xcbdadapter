import React from "react";
import {Component} from "react";
import {Progress} from "reactstrap";
import axios from "axios";
import "./FileUpload.css";
import SelectBox from "../features/select-box";

class FileUpload extends Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedFile: null,
            configurations: null,
            isConfig: this.props.type === "csv" ? false : true,
            csvConfigurationName: "cvs",
            listConfigurationName: []
        };
        this.getConfigurationNameList(this.state.isConfig);
    }

    onSelectConfig = (name) => {
        if (this.setState.isConfig) {
            console.log("onSelectConfig: Config: ", name);
            this.getConfiguration(name);
        } else {
            console.log("onSelectConfig: Config: ", name);
            this.setState({csvConfigurationName: name});
        }
    }

    getConfiguration(name) {
        var url = "/api/configuration/" + name;
        console.log("getConfiguration: " + url);
        axios.get(url).then(res => {
            console.log(res.data);
        });
    }

    getConfigurationNameList(isCSVType) {
        var url = isCSVType ? "/api/listCSVConfigurationName" : "/api/listConfigurationName";
        axios.get(url).then(res => {
            var list = [];
            for (var i = 0; i < res.data.length; i++) {
                list[i] = {value: res.data[i], id: i + 1};
            }
            this.setState({listConfigurationName: list});
        });
    }

    componentWillReceiveProps() {
        this.setState.isConfig = this.props.type === "csv" ? false : true;
        if (!this.state.isConfig) {
            this.getCSVConfigurationName();
        } else {
            this.setState({listConfigurationName: []});
        }
    }

    onChangeHandler = event => {
        this.setState({
            selectedFile: event.target.files,
            loaded: 0
        });
    };

    onClickHandler = () => {
        var data = new FormData();
        for (var i = 0; i < this.state.selectedFile.length; i++) {
            data.append("files", this.state.selectedFile[i]);
        }

        if (this.state.isConfig) {
            axios.post("/api/uploadMultiConfig", data, {
                onUploadProgress: ProgressEvent => {
                    this.setState({
                        loaded: (ProgressEvent.loaded / ProgressEvent.total) * 100
                    });
                }
            })
                .then(res => {
                    // then print response status
                    console.log(res);
                });
        } else {
            data.append("config_name", this.state.csvConfigurationName);
            axios.post("/api/uploadMultiCSVFile", data, {
                onUploadProgress: ProgressEvent => {
                    this.setState({
                        loaded: (ProgressEvent.loaded / ProgressEvent.total) * 100
                    });
                }
            })
                .then(res => {
                    // then print response status
                    console.log(res);
                });
        }
    };

    render() {
        return (
            < div
        className = 'container' >
            < div
        className = 'row' >
            < div
        className = 'col-md-6' >
            < form
        method = 'post'
        action = '#'
        id = '#' >
            < div
        className = 'form-group files' >
            < label > Upload
        {
            this.state.isConfig ? "Configuration" : "CSV"
        }
        File(s) < /label>
        < input
        type = 'file'
        className = 'form-control'
        multiple
        onChange = {this.onChangeHandler}
        />
        < /div>
        < /form>
        < div
        className = 'form-group' >
            < Progress
        max = '100'
        color = 'success'
        value = {this.state.loaded} >
            {Math.round(this.state.loaded, 2)} %
            < /Progress>
            < /div>
            < button
        type = 'button'
        className = 'btn btn-success btn-block'
        onClick = {this.onClickHandler} >
            Upload
            < /button>
            < /div>
            < /div>
            < div >
            {
                this.state.isConfig ? (
                    < div className = "row" >
                    < div className = 'col-md-6' >
                < div >
                {
                    this.state.listConfigurationName.length === 0 ? null : (
                        < div style = {
        {
            margin: "16px", position
        :
            "relative"
        }
    }>
    <
        h3 > Configuration
        Name < /h3>
        < SelectBox
        items = {this.state.listConfigurationName}
        onSelectItem = {this.onSelectConfig}
        />
        < /div>
    )
    }
    <
        /div>
        < /div>
        < /div>
    ) :
        (
        < div
        className = 'row' >
            < div
        className = 'col-md-6' >
            < div >
            {
                this.state.listConfigurationName.length === 0 ? null : (
                    < div style = {
        {
            margin: "16px", position
        :
            "relative"
        }
    }>
    <
        h3 > CSV
        Configuration < /h3>
        < SelectBox
        items = {this.state.listConfigurationName}
        onSelectItem = {this.onSelectConfig}
        />
        < /div>
    )
    }
    <
        /div>
        < /div>
        < /div>
    )
    }
    <
        /div>
        < /div>
    )
        ;
    }
}

export default FileUpload;
