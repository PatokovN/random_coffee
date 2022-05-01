package com.epam.random_coffee.events.di

import com.epam.random_coffee.events.api.RcEventsAPI

class ApiDI(serviceDI: ServiceDI) {

  lazy val rcEventsApi: RcEventsAPI = new RcEventsAPI(serviceDI.eventService)

}
