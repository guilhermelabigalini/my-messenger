//// https://stackoverflow.com/questions/28241912/bootstrap-modal-in-react-js


//class Alert extends Component {
//    constructor(props) {
//        super(props);
//    }
//    componentDidMount() {
//        $(this.modal).modal('show');
//        $(this.modal).on('hidden.bs.modal', handleModalCloseClick);
//    }
//    render() {
//        return (
//            <div>
//                <div className="modal fade" ref={modal => this.modal = modal}
//                    id="exampleModal" tabIndex="-1" role="dialog" aria- labelledby="exampleModalLabel" aria-hidden="true">
//                    <div className="modal-dialog" role="document">
//                        <div className="modal-content">
//                            <div className="modal-header">
//                                <h5 className="modal-title" id="exampleModalLabel">Modal title
//                                </h5>
//                                <button type="button" className="close" data- dismiss="modal" aria-label="Close">
//                                    <span aria-hidden="true">&times;</span>
//                                </button>
//                            </div>
//                            <div className="modal-body">
//                                ...
//                            </div>
//                            <div className="modal-footer">
//                                <button type="button" className="btn btn-secondary" data- dismiss="modal">Close</button>
//                                <button type="button" className="btn btn-primary">Save changes</button>
//                            </div>
//                        </div>
//                    </div>
//                </div>
//            </div>
//        );
//    }
//}