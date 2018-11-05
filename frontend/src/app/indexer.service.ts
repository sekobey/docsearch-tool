import {Injectable} from '@angular/core';
import {MessageService} from './message.service';
import {IndexableItem} from './indexable.item';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {IndexerResponse} from './indexer.response';
import {Subscription} from 'rxjs/index';


const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class IndexerService {

  private indexerUrl = 'http://localhost:8080/index';
  private indexerFilesUrl = 'http://localhost:8080/index-files';

  constructor(private messageService: MessageService,
              private http: HttpClient) {
  }

  index(items: IndexableItem[]): Subscription {
    this.log(`${items.length} item(s) are sent to Indexer !`);
    return this.http.post<IndexerResponse[]>(this.indexerUrl, items).subscribe(
      (response: IndexerResponse[]) => {
        response.forEach(res => this.log(`Version: ${res.version}, File Name: ${res.fileName}, URL: ${res.url}, ServerResponse: ${res.message}`));
      },
      err => this.log('Error occured: ' + err)
    );
  }

  indexFiles(items: IndexableItem[], fileList: FileList): Subscription {
    const formData: FormData = new FormData();
    for (let i = 0; i < fileList.length; i++) {
      formData.append('files', fileList.item(i));
    }
    formData.append('metadata', JSON.stringify(items));

    this.log(`${items.length} item(s) are sent to Indexer !`);
    return this.http.post<IndexerResponse[]>(this.indexerFilesUrl, formData).subscribe(
      (response: IndexerResponse[]) => {
        response.forEach(res => this.log(`Version: ${res.version}, File Name: ${res.fileName}, URL: ${res.url}, ServerResponse: ${res.message}`));
      },
      err => this.log('Error occured: ' + err)
    );
  }

  /** Log a HeroService message with the MessageService */
  private log(message: string) {
    this.messageService.add(`IndexerService: ${message}`);
  }
}
