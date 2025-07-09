import React from "react";
import { Field, FilterNode, FilterCondition, FilterGroup } from "../../../../types/chart";
import FilterConditionComponent from "./FilterConditionComponent";
import FilterGroupComponent from "./FilterGroupComponent";

interface FilterComponentProps {
    node: FilterNode;
    allSourceFields: Field[];
    onChange: (node: FilterNode) => void;
    onDelete: () => void;
}

const FilterComponent: React.FC<FilterComponentProps> = ({ node, allSourceFields, onChange, onDelete }) => {
    if (node.type === "condition") {
        return (
            <FilterConditionComponent
                condition={node as FilterCondition}
                allSourceFields={allSourceFields}
                onChange={cond => onChange(cond)}
                onDelete={onDelete}
            />
        );
    } else if (node.type === "group") {
        return (
            <FilterGroupComponent
                group={node as FilterGroup}
                allSourceFields={allSourceFields}
                onChange={g => onChange(g)}
                onDelete={onDelete}
            />
        );
    } else {
        return null;
    }
};

export default FilterComponent;