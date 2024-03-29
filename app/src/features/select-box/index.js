import React from "react";

import "./styles.css";

class SelectBox extends React.Component {
    state = {
        items: this.props.items || [],
        showItems: false,
        selectedItem: this.props.items[0] || this.props.selectedItem,
        onSelectItem: this.props.onSelectItem
    };

    dropDown = () => {
        this.setState(prevState => ({
            showItems: !prevState.showItems
        }));
    };

    selectItem = item => {
        this.setState({
            selectedItem: item,
            showItems: false
        });
        this.state.onSelectItem(item.value);
    };

    render() {
        return (
            < div >
            < div
        className = "select-box--box" >
            < div
        className = "select-box--container" >
            < div
        className = "select-box--selected-item" > {this.state.selectedItem.value} < /div>
            < div
        className = "select-box--arrow"
        onClick = {this.dropDown} >
            < span
        className = {`${
            this.state.showItems ? "select-box--arrow-up" : "select-box--arrow-down"
        }`
    }
        />
        < /div>
        < /div>
        < div
        className = "select-box--items"
        style = {
        {
            display: this.state.showItems ? "block" : "none"
        }
    }>
        {
            this.state.items.map(item => (
                < div
            key = {item.id}
            onClick = {() => this.selectItem(item)
        }
            className = {this.state.selectedItem === item ? "selected" : ""}
                >
                {item.value}
                < /div>
        ))
        }
    <
        /div>
        < /div>
        < input
        type = "hidden"
        name = {this.state.name}
        value = {this.state.selectedItem.id}
        />
        < /div>
    )
        ;
    }
}

export default SelectBox;
