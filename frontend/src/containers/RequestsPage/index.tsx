import React, {FC, useEffect, useState} from "react";
import {IAppState} from "../../models/IAppState";
import {connect, ConnectedProps} from "react-redux";
import {loadQuestionnaireRequestsRoutine} from "../../sagas/report/routines";
import UICard from "../../components/UI/UICard";
import UIContent from "../../components/UI/UIContent";
import UIPageTitle from "../../components/UI/UIPageTitle";
import {RequestItem} from "./RequestItem";
import {Tab} from "semantic-ui-react";
import {IRequestShort} from "models/report/IReport";
import styles from './styles.module.sass';

const RequestsPage: FC<RequestPageProps & { match }> = (
    {loadRequests, match, requests}) => {

    const [open, setOpen] = useState([] as IRequestShort[]);
    const [closed, setClosed] = useState([] as IRequestShort[]);

    useEffect(() => {
        loadRequests(match.params.id);
    }, [loadRequests, match.params.id]);

    useEffect(() => {
        setOpen(requests.filter(r => !r.closeDate));
        setClosed(requests.filter(r => r.closeDate));
    }, [requests]);

    const panes = [
        open.length && {
            menuItem: 'Pending requests',
            render: () => <Tab.Pane attached={false}>{open.map(r => (
                <RequestItem request={r}/>
            ))}</Tab.Pane>
        },
        closed.length && {
            menuItem: 'Expired requests',
            render: () => <Tab.Pane attached={false}>{closed.map(r => (
                <RequestItem request={r}/>
            ))}</Tab.Pane>
        }
    ];

    return (
        <>
            <UIPageTitle title="Requests"/>
                <UIContent>
                    <Tab menu={{secondary: true, pointing: true}} panes={panes}/>
                </UIContent>
        </>
    );
};

const mapState = (state: IAppState) => ({
    requests: state.questionnaireReports.list
});
const mapDispatch = {
    loadRequests: loadQuestionnaireRequestsRoutine
};
const connector = connect(mapState, mapDispatch);
type RequestPageProps = ConnectedProps<typeof connector>;
export default connector(RequestsPage);
