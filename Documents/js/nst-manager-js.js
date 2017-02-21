var mySwiper = new Swiper ('.swiper-container', {
    direction: 'horizontal',
    lazyLoading: true,
    loop: true,
    slidesPerView: '1',
    centeredSlides: true,
    spaceBetween: 100,
    autoHeight: false,
    iOSEdgeSwipeDetection: true,
    parallax: true,
    slideToClickedSlide: true,

    effect: 'coverflow',
    coverflow: {
      rotate: 50,
      stretch: 0,
      depth: 100,
      modifier: 1,
      slideShadows : true
    },
    onTap: (swiper, event) => {
        let element = event.target;
        swiper.slideNext()
    },
    pagination: '.swiper-pagination',
    paginationHide: false,
    paginationClickable: true
})

function reloadCamPage() {
    var url = "https://"
    url += window.location.host.toString()
    url += "/api/devices/${device?.getId()}/getCamHTML"
    window.location = url;
}

function reloadTstatPage() {
    var url = "https://"
    url += window.location.host.toString()
    url += "/api/devices/${device?.getId()}/graphHTML"
    window.location = url;
}

function reloadWeatherPage() {
    var url = "https://"
    url += window.location.host.toString()
    url += "/api/devices/${device?.getId()}/getWeatherHTML"
    window.location = url;
}
