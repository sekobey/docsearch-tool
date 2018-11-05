import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {AppRoutingModule} from './/app-routing.module';
import {MatExpansionModule, MatListModule} from '@angular/material';
import {MatTabsModule} from '@angular/material/tabs';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';

import {AppComponent} from './app.component';
import {IndexerComponent} from './indexer/indexer.component';
import {MessagesComponent} from './messages/messages.component';
import {SearchComponent} from './search/search.component';
import {FilesIndexerComponent} from './files-indexer/files-indexer.component';


@NgModule({
  declarations: [
    AppComponent,
    IndexerComponent,
    MessagesComponent,
    SearchComponent,
    FilesIndexerComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatExpansionModule,
    MatListModule,
    MatTabsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
