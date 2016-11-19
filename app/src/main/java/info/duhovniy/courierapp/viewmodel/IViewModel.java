package info.duhovniy.courierapp.viewmodel;


interface IViewModel {

    void onResume();

    void onPause();

    void handleError(Throwable throwable);
}
